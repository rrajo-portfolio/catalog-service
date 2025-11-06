package com.portfolio.catalog.search;

import com.portfolio.catalog.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductDocumentMapper {

    public ProductDocument fromEntity(ProductEntity entity) {
        return ProductDocument.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .sku(entity.getSku())
            .price(entity.getPrice())
            .currency(entity.getCurrency())
            .status(entity.getStatus().name())
            .stockQuantity(entity.getStockQuantity())
            .tags(entity.getTags())
            .build();
    }
}
