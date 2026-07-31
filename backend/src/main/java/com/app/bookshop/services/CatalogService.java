package com.app.bookshop.services;

import java.util.List;
import java.util.Optional;

import com.app.bookshop.entity.Country;
import com.app.bookshop.entity.Product;
import com.app.bookshop.entity.ProductCategory;
import com.app.bookshop.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogService {

    List<ProductCategory> getProductCategories();

    Optional<Product> findByProductId(Long id);

    Page<Product> getProducts(Pageable p);

    Page<Product> findByCategoryId(Long id, Pageable p);

    Page<Product> findByNameContaining(String name, Long categoryId, Pageable p);

    List<Country> getCountries();

    List<State> getStatesByCountryCode(String countryCode);
}
