package com.app.bookshop.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Purchase(
    @NotNull @Valid CustomerRequest customer,
    @NotNull @Valid AddressRequest shippingAddress,
    @NotNull @Valid AddressRequest billingAddress,
    @NotNull @NotEmpty @Size(max = 50) @Valid List<OrderItemRequest> orderItems,
    @NotBlank @Size(max = 8) String currencyCode
) {
}
