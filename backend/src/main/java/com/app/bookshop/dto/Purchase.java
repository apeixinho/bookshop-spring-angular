package com.app.bookshop.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record Purchase(
    @NotNull @Valid CustomerRequest customer,
    @NotNull @Valid AddressRequest shippingAddress,
    @NotNull @Valid AddressRequest billingAddress,
    @NotNull @NotEmpty @Valid List<OrderItemRequest> orderItems,
    @NotBlank String currencyCode
) {
}
