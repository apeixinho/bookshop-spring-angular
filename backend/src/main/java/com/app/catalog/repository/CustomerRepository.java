package com.app.catalog.repository;

import java.util.Optional;

import com.app.catalog.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByOauthSub(String oauthSub);
}
