package com.app.bookshop.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.app.bookshop.i18n.SupportedLocale;
import com.app.bookshop.mapper.CountryMapper;
import com.app.bookshop.mapper.ProductMapper;
import com.app.bookshop.mapper.StateMapper;
import com.app.bookshop.model.CountryDTO;
import com.app.bookshop.model.ProductCategoryDTO;
import com.app.bookshop.model.ProductDTO;
import com.app.bookshop.model.StateDTO;
import com.app.bookshop.services.CatalogService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {

    private static final String LOCALE_KEY =
        "T(com.app.bookshop.i18n.SupportedLocale).normalize(#lang)";

    private final String API_PATH = "/api/v1";
    private final CatalogService catalogService;
    private final ProductMapper productMapper;
    private final StateMapper stateMapper;
    private final CountryMapper countryMapper;

    public CatalogController(
        CatalogService catalogService,
        ProductMapper productMapper,
        StateMapper stateMapper,
        CountryMapper countryMapper) {
        this.catalogService = catalogService;
        this.productMapper = productMapper;
        this.stateMapper = stateMapper;
        this.countryMapper = countryMapper;
    }

    @GetMapping(API_PATH + "/products/search/findByCategoryId")
    @Cacheable(value = "productFindByCategory", key = "{#page, #size, #id, " + LOCALE_KEY + "}")
    public Page<ProductDTO> searchProductByCategoryId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam long id,
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);
        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .findByCategoryId(id, currentPage)
            .map(product -> productMapper.productToProductDto(product, locale));
    }

    @GetMapping(API_PATH + "/products/search/findByNameContaining")
    @Cacheable(
        value = "productFindByName",
        key = "{#page, #size, #name, #categoryId, " + LOCALE_KEY + "}")
    public Page<ProductDTO> searchProductByName(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam String name,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);
        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .findByNameContaining(name, categoryId, currentPage)
            .map(product -> productMapper.productToProductDto(product, locale));
    }

    @GetMapping(API_PATH + "/product-category")
    @Cacheable(value = "product-category", key = LOCALE_KEY)
    public List<ProductCategoryDTO> getProductCategories(
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);

        return catalogService
            .getProductCategories()
            .stream()
            .map(category -> productMapper.productCategoryToProductCategoryDto(category, locale))
            .collect(Collectors.toList());
    }

    @GetMapping(API_PATH + "/products")
    @Cacheable(value = "products", key = "{#page, #size, " + LOCALE_KEY + "}")
    public Page<ProductDTO> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);
        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .getProducts(currentPage)
            .map(product -> productMapper.productToProductDto(product, locale));
    }

    @GetMapping(API_PATH + "/products/{id}")
    @Cacheable(
        value = "productFindById",
        key = "{#id, " + LOCALE_KEY + "}",
        unless = "#result == null || #result.isEmpty()")
    public Optional<ProductDTO> getProductById(
        @PathVariable long id,
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);

        return catalogService
            .findByProductId(id)
            .map(product -> productMapper.productToProductDto(product, locale));
    }

    @GetMapping(API_PATH + "/states/search/findByCountryCode")
    @Cacheable(value = "stateByCountryCode", key = "{#code, " + LOCALE_KEY + "}")
    public List<StateDTO> getStatesByCountryCode(
        @RequestParam String code,
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);

        return catalogService
            .getStatesByCountryCode(code.toUpperCase())
            .stream()
            .map(state -> stateMapper.stateToStateDto(state, locale))
            .collect(Collectors.toList());
    }

    @GetMapping(API_PATH + "/countries")
    @Cacheable(value = "countries", key = LOCALE_KEY)
    public List<CountryDTO> getCountries(
        @RequestParam(required = false) String lang) {

        String locale = SupportedLocale.normalize(lang);

        return catalogService
            .getCountries()
            .stream()
            .map(country -> countryMapper.countryToCountryDto(country, locale))
            .collect(Collectors.toList());
    }
}
