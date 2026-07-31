package com.app.bookshop.services;

import com.app.bookshop.entity.Order;
import com.app.bookshop.entity.OrderStatus;
import com.app.bookshop.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPaymentCancelService {

    private final OrderRepository orderRepository;

    public OrderPaymentCancelService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCancelled(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.saveAndFlush(order);
        }
    }
}
