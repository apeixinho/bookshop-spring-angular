package com.apeixinho.bookshop.repository;

import com.apeixinho.bookshop.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CountryRepository extends JpaRepository<Country, Integer> {
}
