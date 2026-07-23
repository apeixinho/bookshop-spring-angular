package com.apeixinho.bookshop.repository;

import com.apeixinho.bookshop.entity.State;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface StateRepository extends JpaRepository<State, Integer> {

    List<State> findByCountryCode(String code);

}
