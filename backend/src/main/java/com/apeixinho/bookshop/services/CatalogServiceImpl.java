package com.apeixinho.bookshop.services;

import java.util.List;
import java.util.Optional;

import com.apeixinho.bookshop.entity.Country;
import com.apeixinho.bookshop.entity.Product;
import com.apeixinho.bookshop.entity.ProductCategory;
import com.apeixinho.bookshop.entity.State;
import com.apeixinho.bookshop.repository.CountryRepository;
import com.apeixinho.bookshop.repository.ProductCategoryRepository;
import com.apeixinho.bookshop.repository.ProductRepository;
import com.apeixinho.bookshop.repository.StateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @Override
    public Page<Product> getProducts(Pageable page) {

        return productRepository.findAll(page);
    }

    @Override
    public Optional<Product> findByProductId(Long id) {

        return productRepository.findById(id);
    }

    @Override
    public List<ProductCategory> getProductCategories() {

        return productCategoryRepository.findAll();
    }

    @Override
    public Page<Product> findByCategoryId(Long id, Pageable page) {

        return productRepository.findByCategoryId(id, page);
    }

    @Override
    public Page<Product> findByNameContaining(String name, Pageable page) {

        return productRepository.findByNameContaining(name, page);
    }

    @Override
    public List<Country> getCountries() {

        return countryRepository.findAll();
    }

    @Override
    public List<State> getStatesByCountryCode(String countryCode) {

        return stateRepository.findByCountryCode(countryCode);
    }

}
