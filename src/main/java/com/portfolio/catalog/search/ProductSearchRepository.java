package com.portfolio.catalog.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, UUID> {

    List<ProductDocument> findTop20ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
