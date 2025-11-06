package com.portfolio.catalog.service;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.ProductSummary;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, OffsetDateTime.class, ProductStatus.class})
public interface ProductMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", expression = "java(ProductStatus.ACTIVE)")
    @Mapping(target = "lastUpdatedAt", expression = "java(OffsetDateTime.now())")
    ProductEntity toEntity(CreateProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateProductRequest request, @MappingTarget ProductEntity entity);

    Product toProduct(ProductEntity entity);

    ProductSummary toSummary(ProductEntity entity);

    @AfterMapping
    default void touchTimestamp(@MappingTarget ProductEntity entity) {
        entity.setLastUpdatedAt(OffsetDateTime.now());
    }
}
