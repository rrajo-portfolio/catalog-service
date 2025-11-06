package com.portfolio.catalog.events;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEventMetricsTest {

    @Test
    @DisplayName("increment should update counters per type")
    void incrementCounters() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ProductEventMetrics metrics = new ProductEventMetrics(registry);

        metrics.increment(ProductEventType.UPSERT);
        metrics.increment(ProductEventType.DELETE);
        metrics.increment(ProductEventType.UPSERT);

        double upsert = registry.get("catalog.product.events").tag("type", "UPSERT").counter().count();
        double delete = registry.get("catalog.product.events").tag("type", "DELETE").counter().count();

        assertThat(upsert).isEqualTo(2d);
        assertThat(delete).isEqualTo(1d);
    }
}
