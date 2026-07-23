package com.apeixinho.bookshop.controller;

import lombok.RequiredArgsConstructor;
import com.apeixinho.bookshop.mapper.CountryMapper;
import com.apeixinho.bookshop.mapper.ProductMapper;
import com.apeixinho.bookshop.mapper.StateMapper;
import com.apeixinho.bookshop.model.CountryDTO;
import com.apeixinho.bookshop.model.ProductCategoryDTO;
import com.apeixinho.bookshop.model.ProductDTO;
import com.apeixinho.bookshop.model.StateDTO;
import com.apeixinho.bookshop.services.CatalogService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class CatalogController {

    private final String API_PATH = "/api/v1";
    private final CatalogService catalogService;
    private final ProductMapper productMapper;
    private final StateMapper stateMapper;
    private final CountryMapper countryMapper;

    @GetMapping(API_PATH + "/products/search/findByCategoryId")
    @Cacheable(value = "productFindByCategory")
    public Page<ProductDTO> searchProductByCategoryId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam long id) {

        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .findByCategoryId(id, currentPage)
            .map(productMapper::productToProductDto);
    }

    @GetMapping(API_PATH + "/products/search/findByNameContaining")
    @Cacheable(value = "productFindByName")
    public Page<ProductDTO> searchProductByName(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam String name) {

        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .findByNameContaining(name, currentPage)
            .map(productMapper::productToProductDto);
    }

    @GetMapping(API_PATH + "/product-category")
    @Cacheable(value = "product-category")
    public List<ProductCategoryDTO> getProductCategories() {

        return catalogService
            .getProductCategories()
            .stream()
            .map(productMapper::productCategoryToProductCategoryDto)
            .collect(Collectors.toList());

    }

    @GetMapping(API_PATH + "/products")
    @Cacheable(value = "products")
    public Page<ProductDTO> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

        Pageable currentPage = PageRequest.of(page, size);

        return catalogService
            .getProducts(currentPage)
            .map(productMapper::productToProductDto);

    }

    @GetMapping(API_PATH + "/products/{id}")
    @Cacheable(value = "productFindById")
    public Optional<ProductDTO> getProductById(
        @PathVariable long id) {

        return catalogService
            .findByProductId(id)
            .map(productMapper::productToProductDto);

    }

    @GetMapping(API_PATH + "/states/search/findByCountryCode")
    @Cacheable(value = "stateByCountryCode")
    public List<StateDTO> getStatesByCountryCode(
        @RequestParam String code) {

        return catalogService
            .getStatesByCountryCode(code.toUpperCase())
            .stream()
            .map(stateMapper::stateToStateDto)
            .collect(Collectors.toList());

    }

    @GetMapping(API_PATH + "/countries")
    @Cacheable(value = "countries")
    public List<CountryDTO> getCountries() {

        return catalogService
            .getCountries()
            .stream()
            .map(countryMapper::countryToCountryDto)
            .collect(Collectors.toList());

    }
}
