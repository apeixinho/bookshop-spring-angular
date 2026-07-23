package com.apeixinho.bookshop.repository;

import com.apeixinho.bookshop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
