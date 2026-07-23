package com.apeixinho.bookshop.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StateDTO {

    @NotBlank
    @NotNull
    private int id;

    @NotBlank
    @NotNull
    private String name;

}
