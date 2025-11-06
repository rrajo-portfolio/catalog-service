package com.portfolio.catalog.events;

import com.portfolio.catalog.config.CatalogEventsProperties;
import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final CatalogEventsProperties properties;
    private final ProductDocumentMapper documentMapper;

    public void publishUpsert(ProductEntity entity) {
        ProductDocument document = documentMapper.fromEntity(entity);
        ProductEvent event = new ProductEvent(entity.getId(), ProductEventType.UPSERT, document);
        kafkaTemplate.send(properties.productTopic(), entity.getId().toString(), event);
        log.debug("Published UPSERT event for product {}", entity.getId());
    }

    public void publishDelete(UUID productId) {
        ProductEvent event = new ProductEvent(productId, ProductEventType.DELETE, null);
        kafkaTemplate.send(properties.productTopic(), productId.toString(), event);
        log.debug("Published DELETE event for product {}", productId);
    }
}
