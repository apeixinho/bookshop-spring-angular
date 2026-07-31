package com.app.bookshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest(
    @NotBlank @Size(max = 255) String street,
    @NotBlank @Size(max = 255) String city,
    /** Stable catalog state id (not a localized display name). */
    @NotNull Integer stateId,
    @NotBlank @Size(max = 16) String countryCode,
    @NotBlank @Size(max = 32) String zipCode
) {
}
