package com.portfolio.catalog.repository;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("findBySkuIgnoreCase should return matching product")
    void findBySkuIgnoreCaseReturnsProduct() {
        ProductEntity entity = baseProduct("PORT-001")
            .name("Portfolio Booster")
            .build();
        productRepository.save(entity);

        Optional<ProductEntity> result = productRepository.findBySkuIgnoreCase("port-001");

        assertThat(result).isPresent();
        assertThat(result.get().getSku()).isEqualTo("PORT-001");
    }

    @Test
    @DisplayName("findTop10ByStatusOrderByLastUpdatedAtDesc should return the most recent ACTIVE products")
    void findTop10ByStatusOrderByLastUpdatedAtDescReturnsRecentProducts() {
        for (int index = 0; index < 12; index++) {
            productRepository.save(baseProduct("SKU-%02d".formatted(index))
                .lastUpdatedAt(OffsetDateTime.now().minusDays(index))
                .build());
        }

        List<ProductEntity> result = productRepository.findTop10ByStatusOrderByLastUpdatedAtDesc(ProductStatus.ACTIVE);

        assertThat(result)
            .hasSize(10)
            .isSortedAccordingTo((a, b) -> b.getLastUpdatedAt().compareTo(a.getLastUpdatedAt()));
        assertThat(result.stream().map(ProductEntity::getSku))
            .containsExactly("SKU-00", "SKU-01", "SKU-02", "SKU-03", "SKU-04",
                "SKU-05", "SKU-06", "SKU-07", "SKU-08", "SKU-09");
    }

    private ProductEntity.ProductEntityBuilder baseProduct(String sku) {
        return ProductEntity.builder()
            .name("Sample %s".formatted(sku))
            .description("Test product %s".formatted(sku))
            .sku(sku)
            .price(BigDecimal.valueOf(99.99))
            .currency("EUR")
            .stockQuantity(5)
            .status(ProductStatus.ACTIVE)
            .tags(List.of("spring"))
            .lastUpdatedAt(OffsetDateTime.now());
    }
}
