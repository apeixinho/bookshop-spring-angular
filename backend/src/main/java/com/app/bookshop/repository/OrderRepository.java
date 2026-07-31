package com.app.bookshop.repository;

import com.app.bookshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderTrackingNumber(String orderTrackingNumber);
}
