package com.apeixinho.bookshop.mapper;

import com.apeixinho.bookshop.entity.Product;
import com.apeixinho.bookshop.entity.ProductCategory;
import com.apeixinho.bookshop.model.ProductCategoryDTO;
import com.apeixinho.bookshop.model.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    Product productDtoToProduct(ProductDTO p);

    ProductDTO productToProductDto(Product p);

    @Mapping(target = "products", ignore = true)
    ProductCategory productCategoryDtoToProductCategory(ProductCategoryDTO p);

    ProductCategoryDTO productCategoryToProductCategoryDto(ProductCategory p);
    
}
