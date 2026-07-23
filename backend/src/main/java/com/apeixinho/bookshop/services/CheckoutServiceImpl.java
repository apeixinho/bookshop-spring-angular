package com.apeixinho.bookshop.services;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.apeixinho.bookshop.dto.Purchase;
import com.apeixinho.bookshop.dto.PurchaseResponse;
import com.apeixinho.bookshop.entity.Customer;
import com.apeixinho.bookshop.entity.Order;
import com.apeixinho.bookshop.entity.OrderItem;
import com.apeixinho.bookshop.entity.Product;
import com.apeixinho.bookshop.repository.CustomerRepository;
import com.apeixinho.bookshop.repository.OrderRepository;
import com.apeixinho.bookshop.repository.ProductRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Places an order. Payment / stock decrement are intentionally incomplete in
 * this phase; catalog prices are always applied from the database so the client
 * cannot set its own unit/total prices (dev-safe mock until payments land).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase payload is required");
        }
        if (purchase.getOrder() == null) {
            throw new IllegalArgumentException("Order is required");
        }
        if (purchase.getOrderItems() == null || purchase.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }
        if (purchase.getCustomer() == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (purchase.getBillingAddress() == null || purchase.getShippingAddress() == null) {
            throw new IllegalArgumentException("Billing and shipping addresses are required");
        }

        Order order = purchase.getOrder();
        applyCatalogPricing(purchase.getOrderItems(), order);

        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);
        order.setStatus("PENDING"); // payment not integrated yet

        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(order::add);

        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        Customer customer = purchase.getCustomer();
        customer.add(order);

        customerRepository.save(customer);

        log.info("Dev checkout placed tracking={} total={} qty={} (payment mocked as PENDING)",
            orderTrackingNumber, order.getTotalPrice(), order.getTotalQuantity());

        return new PurchaseResponse(orderTrackingNumber);
    }

    /**
     * Overwrites client-supplied prices with catalog values. Stock is not
     * decremented yet — incomplete checkout flow.
     */
    private void applyCatalogPricing(Set<OrderItem> orderItems, Order order) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (OrderItem item : orderItems) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Each order item requires a productId");
            }
            if (item.getQuantity() < 1) {
                throw new IllegalArgumentException("Order item quantity must be at least 1");
            }

            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unknown productId: " + item.getProductId()));

            item.setUnitPrice(product.getUnitPrice());
            if (item.getImageUrl() == null || item.getImageUrl().isBlank()) {
                item.setImageUrl(product.getImageUrl());
            }

            BigDecimal lineTotal = product.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(lineTotal);
            totalQuantity += item.getQuantity();
        }

        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
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
