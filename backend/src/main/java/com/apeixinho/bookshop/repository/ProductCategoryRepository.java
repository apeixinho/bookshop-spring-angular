package com.apeixinho.bookshop.repository;

import com.apeixinho.bookshop.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

}
