package com.apeixinho.bookshop.services;

import java.util.List;
import java.util.Optional;

import com.apeixinho.bookshop.entity.Country;
import com.apeixinho.bookshop.entity.Product;
import com.apeixinho.bookshop.entity.ProductCategory;
import com.apeixinho.bookshop.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogService {

    List<ProductCategory> getProductCategories();

    Optional<Product> findByProductId(Long id);

    Page<Product> getProducts(Pageable p);

    Page<Product> findByCategoryId(Long id, Pageable p);

    Page<Product> findByNameContaining(String s, Pageable p);

    List<Country> getCountries();

    List<State> getStatesByCountryCode(String countryCode);
}
