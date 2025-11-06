package com.portfolio.catalog.events;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductEventMetrics {

    private final Counter upsertCounter;
    private final Counter deleteCounter;

    public ProductEventMetrics(MeterRegistry registry) {
        this.upsertCounter = Counter.builder("catalog.product.events")
            .tag("type", ProductEventType.UPSERT.name())
            .description("Number of product UPSERT events processed")
            .register(registry);
        this.deleteCounter = Counter.builder("catalog.product.events")
            .tag("type", ProductEventType.DELETE.name())
            .description("Number of product DELETE events processed")
            .register(registry);
    }

    public void increment(ProductEventType type) {
        if (type == ProductEventType.DELETE) {
            deleteCounter.increment();
            return;
        }
        upsertCounter.increment();
    }
}
