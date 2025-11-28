package com.portfolio.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void toEntityInitializesDefaults() {
        CreateProductRequest request = new CreateProductRequest()
            .name("Portfolio Health Check")
            .sku("PORT-1")
            .currency("EUR")
            .price(49.99)
            .stockQuantity(10)
            .description("Enterprise showcase")
            .tags(List.of("spring", "kafka"));

        ProductEntity entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(entity.getVersion()).isZero();
        assertThat(entity.getLastUpdatedAt()).isNotNull();
        assertThat(entity.getTags()).containsExactly("spring", "kafka");
    }

    @Test
    void updateEntityCopiesNonNullValuesAndTouchesTimestamp() {
        OffsetDateTime previousUpdate = OffsetDateTime.now().minusDays(1);
        ProductEntity entity = ProductEntity.builder()
            .id(java.util.UUID.randomUUID())
            .name("Legacy")
            .description("Pending")
            .sku("LEG-1")
            .price(BigDecimal.TEN)
            .currency("USD")
            .stockQuantity(1)
            .status(ProductStatus.ACTIVE)
            .lastUpdatedAt(previousUpdate)
            .version(1L)
            .build();

        UpdateProductRequest request = new UpdateProductRequest()
            .name("Updated name")
            .description("Updated desc")
            .stockQuantity(5);

        mapper.updateEntity(request, entity);

        assertThat(entity.getName()).isEqualTo("Updated name");
        assertThat(entity.getDescription()).isEqualTo("Updated desc");
        assertThat(entity.getStockQuantity()).isEqualTo(5);
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getLastUpdatedAt()).isAfter(previousUpdate);
    }

    @Test
    void toProductMapsBasicFields() {
        ProductEntity entity = ProductEntity.builder()
            .id(java.util.UUID.randomUUID())
            .name("Portfolio Accelerator")
            .description("Premium")
            .sku("ACC-7")
            .currency("EUR")
            .price(BigDecimal.valueOf(199))
            .stockQuantity(3)
            .status(ProductStatus.ACTIVE)
            .lastUpdatedAt(OffsetDateTime.now())
            .version(2L)
            .build();

        Product dto = mapper.toProduct(entity);

        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getSku()).isEqualTo("ACC-7");
        assertThat(dto.getStatus()).isEqualTo(Product.StatusEnum.ACTIVE);
    }
}
