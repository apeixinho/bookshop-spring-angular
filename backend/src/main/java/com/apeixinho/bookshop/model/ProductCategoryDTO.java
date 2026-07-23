package com.apeixinho.bookshop.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ProductCategoryDTO {

    @NotBlank
    @NotNull
    private Long id;

    @NotBlank
    @NotNull
    private String categoryName;

}
