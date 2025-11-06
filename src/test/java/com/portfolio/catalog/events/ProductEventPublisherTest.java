package com.portfolio.catalog.events;

import com.portfolio.catalog.config.CatalogEventsProperties;
import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductDocumentMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductEventPublisherTest {

    @Mock
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Mock
    private CatalogEventsProperties properties;

    @Mock
    private ProductDocumentMapper documentMapper;

    @InjectMocks
    private ProductEventPublisher publisher;

    private ProductEntity entity;
    private ProductDocument document;

    @BeforeEach
    void setUp() {
        entity = ProductEntity.builder()
            .id(UUID.randomUUID())
            .name("Portfolio API")
            .description("Complete demo")
            .sku("PORT-API")
            .price(BigDecimal.TEN)
            .currency("EUR")
            .stockQuantity(5)
            .status(ProductStatus.ACTIVE)
            .tags(List.of("spring"))
            .lastUpdatedAt(OffsetDateTime.now())
            .build();
        document = ProductDocument.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
        when(properties.productTopic()).thenReturn("catalog-product-events");
    }

    @Test
    @DisplayName("publishUpsert sends the document to Kafka")
    void publishUpsert() {
        when(documentMapper.fromEntity(entity)).thenReturn(document);
        publisher.publishUpsert(entity);

        ArgumentCaptor<ProductEvent> captor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("catalog-product-events"), eq(entity.getId().toString()), captor.capture());
        ProductEvent payload = captor.getValue();
        assertThat(payload.type()).isEqualTo(ProductEventType.UPSERT);
        assertThat(payload.payload()).isEqualTo(document);
    }

    @Test
    @DisplayName("publishDelete sends a delete command to Kafka")
    void publishDelete() {
        publisher.publishDelete(entity.getId());

        ArgumentCaptor<ProductEvent> captor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("catalog-product-events"), eq(entity.getId().toString()), captor.capture());
        ProductEvent payload = captor.getValue();
        assertThat(payload.type()).isEqualTo(ProductEventType.DELETE);
        assertThat(payload.payload()).isNull();
    }
}
