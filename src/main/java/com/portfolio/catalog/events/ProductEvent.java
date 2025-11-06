package com.portfolio.catalog.events;

import com.portfolio.catalog.search.ProductDocument;

import java.util.UUID;

public record ProductEvent(
    UUID id,
    ProductEventType type,
    ProductDocument payload
) {
}
