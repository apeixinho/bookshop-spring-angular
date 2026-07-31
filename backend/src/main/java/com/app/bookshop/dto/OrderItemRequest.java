package com.app.bookshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull Long productId,
    @Min(1) int quantity
) {
}
