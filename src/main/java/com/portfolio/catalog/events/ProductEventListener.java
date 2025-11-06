package com.portfolio.catalog.events;

import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);

    private final ProductSearchRepository searchRepository;

    @KafkaListener(topics = "${catalog.events.product-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handle(ProductEvent event) {
        if (event.type() == ProductEventType.DELETE) {
            searchRepository.deleteById(event.id());
            log.debug("Removed product {} from Elasticsearch index", event.id());
            return;
        }
        ProductDocument payload = event.payload();
        if (payload == null) {
            log.warn("Received UPSERT event without payload for product {}", event.id());
            return;
        }
        searchRepository.save(payload);
        log.debug("Indexed product {} in Elasticsearch", payload.getId());
    }
}
