package com.portfolio.catalog.events;

import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductSearchRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductEventListenerTest {

    @Mock
    private ProductSearchRepository searchRepository;

    @Mock
    private ProductEventMetrics metrics;

    @InjectMocks
    private ProductEventListener listener;

    @Test
    @DisplayName("UPSERT events should persist the document")
    void upsertEventPersistsDocument() {
        UUID id = UUID.randomUUID();
        ProductDocument document = ProductDocument.builder()
            .id(id)
            .name("Portfolio API")
            .sku("PORT-API")
            .price(BigDecimal.TEN)
            .currency("EUR")
            .status("ACTIVE")
            .stockQuantity(10)
            .tags(List.of("spring"))
            .build();
        ProductEvent event = new ProductEvent(id, ProductEventType.UPSERT, document);

        listener.handle(event);

        verify(searchRepository).save(document);
        verify(metrics).increment(ProductEventType.UPSERT);
    }

    @Test
    @DisplayName("DELETE events should remove the document by id")
    void deleteEventRemovesDocument() {
        UUID id = UUID.randomUUID();
        ProductEvent event = new ProductEvent(id, ProductEventType.DELETE, null);

        listener.handle(event);

        verify(searchRepository).deleteById(id);
        verify(metrics).increment(ProductEventType.DELETE);
    }
}
