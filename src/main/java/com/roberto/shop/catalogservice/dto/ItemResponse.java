package com.roberto.shop.catalogservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ItemResponse(
        Long id, String name, String description,
        BigDecimal price, Integer stock, String category,
        Instant updatedAt
) {}
