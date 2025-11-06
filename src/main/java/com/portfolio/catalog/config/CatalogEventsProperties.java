package com.portfolio.catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalog.events")
public record CatalogEventsProperties(
    String productTopic
) {
}
