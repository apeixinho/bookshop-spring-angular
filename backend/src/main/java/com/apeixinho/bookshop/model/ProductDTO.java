package com.apeixinho.bookshop.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDTO {

    @NotBlank
    @NotNull
    private Long id;

    @NotBlank
    @NotNull
    private String sku;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotBlank
    @NotNull
    private BigDecimal unitPrice;

    @NotBlank
    @NotNull
    private String imageUrl;

    @NotBlank
    @NotNull
    private boolean active;

    @NotBlank
    @NotNull
    private int unitsInStock;

    @NotBlank
    @NotNull
    private Date dateCreated;

    @NotBlank
    @NotNull
    private Date lastUpdated;

}
