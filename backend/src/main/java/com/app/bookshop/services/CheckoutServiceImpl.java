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
import com.app.bookshop.entity.Product;
import com.app.bookshop.repository.CustomerRepository;
import com.app.bookshop.repository.OrderRepository;
import com.app.bookshop.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CheckoutServiceImpl(
        CustomerRepository customerRepository,
        OrderRepository orderRepository,
        ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase payload is required");
        }

        String currencyCode = CurrencyRates.normalize(purchase.currencyCode());

        Order order = new Order();
        order.setStatus("PENDING");
        order.setOrderTrackingNumber(generateOrderTrackingNumber());

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (OrderItemRequest itemRequest : purchase.orderItems()) {
            Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unknown productId: " + itemRequest.productId()));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is not available: " + product.getId());
            }
            if (product.getUnitsInStock() < itemRequest.quantity()) {
                throw new IllegalArgumentException(
                    "Insufficient stock for productId: " + product.getId());
            }

            product.setUnitsInStock(product.getUnitsInStock() - itemRequest.quantity());
            productRepository.save(product);

            BigDecimal unitPrice = CurrencyRates.convertFromUsd(product.getUnitPrice(), currencyCode);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(unitPrice);
            item.setImageUrl(product.getImageUrl());
            order.add(item);

            totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity())));
            totalQuantity += itemRequest.quantity();
        }

        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        order.setShippingAddress(toAddress(purchase.shippingAddress()));
        order.setBillingAddress(toAddress(purchase.billingAddress()));

        Customer customer = resolveCustomer(purchase.customer());
        customer.add(order);
        customerRepository.save(customer);

        log.info(
            "Checkout placed tracking={} currency={} total={} qty={} (payment mocked as PENDING)",
            order.getOrderTrackingNumber(),
            currencyCode,
            order.getTotalPrice(),
            order.getTotalQuantity());

        return new PurchaseResponse(order.getOrderTrackingNumber());
    }

    private Customer resolveCustomer(CustomerRequest request) {
        String email = request.email().trim().toLowerCase();
        Customer customer = customerRepository.findByEmailIgnoreCase(email).orElseGet(Customer::new);
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(email);
        return customer;
    }

    private static Address toAddress(AddressRequest request) {
        Address address = new Address();
        address.setStreet(request.street().trim());
        address.setCity(request.city().trim());
        address.setState(request.state().trim());
        address.setCountry(request.country().trim());
        address.setZipCode(request.zipCode().trim());
        return address;
    }

    private String generateOrderTrackingNumber() {
        String orderTrackingNumber;
        Optional<Order> existingOrder;
        do {
            orderTrackingNumber = UUID.randomUUID().toString();
            existingOrder = orderRepository.findByOrderTrackingNumber(orderTrackingNumber);
        } while (existingOrder.isPresent());
        return orderTrackingNumber;
    }
}
