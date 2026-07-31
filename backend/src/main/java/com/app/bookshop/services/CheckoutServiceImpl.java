package com.app.bookshop.services;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.app.bookshop.currency.CurrencyRates;
import com.app.bookshop.dto.AddressRequest;
import com.app.bookshop.dto.CustomerRequest;
import com.app.bookshop.dto.OrderItemRequest;
import com.app.bookshop.dto.Purchase;
import com.app.bookshop.dto.PurchaseResponse;
import com.app.bookshop.entity.Address;
import com.app.bookshop.entity.Customer;
import com.app.bookshop.entity.Order;
import com.app.bookshop.entity.OrderItem;
import com.app.bookshop.entity.OrderStatus;
import com.app.bookshop.entity.Product;
import com.app.bookshop.entity.State;
import com.app.bookshop.i18n.SupportedLocale;
import com.app.bookshop.i18n.TranslationResolver;
import com.app.bookshop.repository.CustomerRepository;
import com.app.bookshop.repository.OrderRepository;
import com.app.bookshop.repository.ProductRepository;
import com.app.bookshop.repository.StateRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StateRepository stateRepository;

    public CheckoutServiceImpl(
        CustomerRepository customerRepository,
        OrderRepository orderRepository,
        ProductRepository productRepository,
        StateRepository stateRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    @Transactional
    @CacheEvict(
        cacheNames = {
            "products",
            "productFindById",
            "productFindByCategory",
            "productFindByName"
        },
        allEntries = true)
    public PurchaseResponse placeOrder(Purchase purchase, String oauthSub, String idempotencyKey) {
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase payload is required");
        }
        if (oauthSub == null || oauthSub.isBlank()) {
            throw new IllegalArgumentException("Authenticated subject is required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }
        if (idempotencyKey.length() > 64) {
            throw new IllegalArgumentException("Idempotency-Key is too long");
        }

        Customer customer = resolveCustomer(purchase.customer(), oauthSub.trim());

        Optional<Order> existing = orderRepository.findByCustomerIdAndIdempotencyKey(
            customer.getId(), idempotencyKey.trim());
        if (existing.isPresent()) {
            return new PurchaseResponse(existing.get().getOrderTrackingNumber());
        }

        String currencyCode = CurrencyRates.normalize(purchase.currencyCode());
        BigDecimal fxRate = CurrencyRates.rate(currencyCode);

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setOrderTrackingNumber(generateOrderTrackingNumber());
        order.setCurrencyCode(currencyCode);
        order.setFxRate(fxRate);
        order.setIdempotencyKey(idempotencyKey.trim());
        order.setShippingAddress(toAddress(purchase.shippingAddress()));
        order.setBillingAddress(toAddress(purchase.billingAddress()));
        order.setCustomer(customer);

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (OrderItemRequest itemRequest : purchase.orderItems()) {
            Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unknown productId: " + itemRequest.productId()));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is not available: " + product.getId());
            }
            if (product.getUnitPrice() == null) {
                throw new IllegalArgumentException("Product has no price: " + product.getId());
            }

            int updated = productRepository.decrementStockIfAvailable(
                product.getId(), itemRequest.quantity());
            if (updated != 1) {
                throw new IllegalArgumentException(
                    "Insufficient stock for productId: " + product.getId());
            }

            BigDecimal unitPrice = CurrencyRates.convertFromUsd(product.getUnitPrice(), currencyCode);
            BigDecimal lineTotal = CurrencyRates.lineTotalFromUsd(
                product.getUnitPrice(), itemRequest.quantity(), currencyCode);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(unitPrice);
            item.setImageUrl(product.getImageUrl());
            order.add(item);

            totalPrice = totalPrice.add(lineTotal);
            totalQuantity += itemRequest.quantity();
        }

        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);

        try {
            orderRepository.saveAndFlush(order);
        } catch (DataIntegrityViolationException ex) {
            Optional<Order> raced = orderRepository.findByCustomerIdAndIdempotencyKey(
                customer.getId(), idempotencyKey.trim());
            if (raced.isPresent()) {
                return new PurchaseResponse(raced.get().getOrderTrackingNumber());
            }
            throw ex;
        }

        log.info(
            "Checkout placed tracking={} currency={} total={} qty={} (payment mocked as PENDING)",
            order.getOrderTrackingNumber(),
            currencyCode,
            order.getTotalPrice(),
            order.getTotalQuantity());

        return new PurchaseResponse(order.getOrderTrackingNumber());
    }

    private Customer resolveCustomer(CustomerRequest request, String oauthSub) {
        String email = request.email().trim().toLowerCase();
        Optional<Customer> bySub = customerRepository.findByOauthSub(oauthSub);
        if (bySub.isPresent()) {
            Customer customer = bySub.get();
            customer.setFirstName(request.firstName().trim());
            customer.setLastName(request.lastName().trim());
            customer.setEmail(email);
            try {
                return customerRepository.saveAndFlush(customer);
            } catch (DataIntegrityViolationException ex) {
                throw new IllegalArgumentException("Email is already linked to another account");
            }
        }

        Optional<Customer> byEmail = customerRepository.findByEmailIgnoreCase(email);
        if (byEmail.isPresent()) {
            Customer customer = byEmail.get();
            if (customer.getOauthSub() != null && !customer.getOauthSub().equals(oauthSub)) {
                throw new IllegalArgumentException("Email is already linked to another account");
            }
            customer.setOauthSub(oauthSub);
            customer.setFirstName(request.firstName().trim());
            customer.setLastName(request.lastName().trim());
            return customerRepository.saveAndFlush(customer);
        }

        Customer customer = new Customer();
        customer.setOauthSub(oauthSub);
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(email);
        try {
            return customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException ex) {
            return customerRepository.findByOauthSub(oauthSub)
                .or(() -> customerRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> ex);
        }
    }

    private Address toAddress(AddressRequest request) {
        String countryCode = request.countryCode().trim().toUpperCase();
        State state = stateRepository.findById(request.stateId())
            .orElseThrow(() -> new IllegalArgumentException("Unknown stateId: " + request.stateId()));
        if (state.getCountry() == null
            || state.getCountry().getCode() == null
            || !state.getCountry().getCode().equalsIgnoreCase(countryCode)) {
            throw new IllegalArgumentException(
                "stateId " + request.stateId() + " does not belong to country " + countryCode);
        }

        Address address = new Address();
        address.setStreet(request.street().trim());
        address.setCity(request.city().trim());
        address.setZipCode(request.zipCode().trim());
        address.setCountry(countryCode);
        // Persist stable English label derived from catalog id (not UI locale).
        String stateLabel = TranslationResolver.stateName(state, SupportedLocale.DEFAULT);
        address.setState(stateLabel.isBlank() ? String.valueOf(state.getId()) : stateLabel);
        return address;
    }

    private String generateOrderTrackingNumber() {
        for (int i = 0; i < 5; i++) {
            String candidate = UUID.randomUUID().toString();
            if (orderRepository.findByOrderTrackingNumber(candidate).isEmpty()) {
                return candidate;
            }
        }
        return UUID.randomUUID().toString();
    }
}
