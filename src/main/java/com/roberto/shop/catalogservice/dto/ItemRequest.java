package com.roberto.shop.catalogservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ItemRequest(
        @NotBlank @Size(max=160) String name,
        @Size(max=500) String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotBlank @Size(max=100) String category
) {}
