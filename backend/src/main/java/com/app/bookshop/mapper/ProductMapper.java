package com.app.bookshop.mapper;

import com.app.bookshop.entity.Product;
import com.app.bookshop.entity.ProductCategory;
import com.app.bookshop.model.ProductCategoryDTO;
import com.app.bookshop.model.ProductDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product productDtoToProduct(ProductDTO p);

    @Mapping(target = "name", expression = "java(com.app.bookshop.i18n.TranslationResolver.productName(p, lang))")
    @Mapping(target = "description", expression = "java(com.app.bookshop.i18n.TranslationResolver.productDescription(p, lang))")
    ProductDTO productToProductDto(Product p, @Context String lang);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "translations", ignore = true)
    ProductCategory productCategoryDtoToProductCategory(ProductCategoryDTO p);

    @Mapping(target = "categoryName", expression = "java(com.app.bookshop.i18n.TranslationResolver.categoryName(p, lang))")
    ProductCategoryDTO productCategoryToProductCategoryDto(ProductCategory p, @Context String lang);
}
