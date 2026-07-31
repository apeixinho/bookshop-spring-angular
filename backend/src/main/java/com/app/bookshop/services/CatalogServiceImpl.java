package com.app.bookshop.services;

import java.util.List;
import java.util.Optional;

import com.app.bookshop.entity.Country;
import com.app.bookshop.entity.Product;
import com.app.bookshop.entity.ProductCategory;
import com.app.bookshop.entity.State;
import com.app.bookshop.repository.CountryRepository;
import com.app.bookshop.repository.ProductCategoryRepository;
import com.app.bookshop.repository.ProductRepository;
import com.app.bookshop.repository.StateRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    public CatalogServiceImpl(
        ProductRepository productRepository,
        ProductCategoryRepository productCategoryRepository,
        CountryRepository countryRepository,
        StateRepository stateRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public Page<Product> getProducts(Pageable page) {
        return productRepository.findByActiveTrue(page).map(this::loadProductTranslations);
    }

    @Override
    public Optional<Product> findByProductId(Long id) {
        return productRepository.findById(id)
            .filter(Product::isActive)
            .map(this::loadProductTranslations);
    }

    @Override
    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.findAll().stream()
            .map(this::loadCategoryTranslations)
            .toList();
    }

    @Override
    public Page<Product> findByCategoryId(Long id, Pageable page) {
        return productRepository.findByCategoryIdAndActiveTrue(id, page).map(this::loadProductTranslations);
    }

    @Override
    public Page<Product> findByNameContaining(String name, Long categoryId, Pageable page) {
        return productRepository
            .findByTranslatedNameContaining(name, categoryId, page)
            .map(this::loadProductTranslations);
    }

    @Override
    public List<Country> getCountries() {
        return countryRepository.findAll().stream()
            .map(this::loadCountryTranslations)
            .toList();
    }

    @Override
    public List<State> getStatesByCountryCode(String countryCode) {
        return stateRepository.findByCountryCode(countryCode).stream()
            .map(this::loadStateTranslations)
            .toList();
    }

    private Product loadProductTranslations(Product product) {
        Hibernate.initialize(product.getTranslations());
        return product;
    }

    private ProductCategory loadCategoryTranslations(ProductCategory category) {
        Hibernate.initialize(category.getTranslations());
        return category;
    }

    private Country loadCountryTranslations(Country country) {
        Hibernate.initialize(country.getTranslations());
        return country;
    }

    private State loadStateTranslations(State state) {
        Hibernate.initialize(state.getTranslations());
        return state;
    }
}
