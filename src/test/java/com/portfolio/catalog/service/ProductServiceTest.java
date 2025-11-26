package com.portfolio.catalog.service;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.exception.ConflictException;
import com.portfolio.catalog.exception.ResourceNotFoundException;
import com.portfolio.catalog.events.ProductEventPublisher;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.ProductAvailabilityRequest;
import com.portfolio.catalog.generated.model.ProductPage;
import com.portfolio.catalog.generated.model.ProductSummary;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import com.portfolio.catalog.repository.ProductRepository;
import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductSearchRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository productSearchRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CatalogMailService catalogMailService;

    @Mock
    private ProductEventPublisher productEventPublisher;

    @InjectMocks
    private ProductService productService;

    private ProductEntity entity;

    @BeforeEach
    void init() {
        entity = ProductEntity.builder()
            .id(UUID.randomUUID())
            .name("Cloud Architect Portfolio")
            .description("Complete enterprise-ready showcase")
            .sku("PORT-999")
            .price(BigDecimal.valueOf(49.5))
            .currency("EUR")
            .stockQuantity(15)
            .status(ProductStatus.ACTIVE)
            .tags(List.of("spring", "portfolio"))
            .lastUpdatedAt(OffsetDateTime.now())
            .build();
    }

    @Test
    @DisplayName("createProduct should persist entity, index document and send notification")
    void createProductPersistsAndNotifies() {
        CreateProductRequest request = new CreateProductRequest()
            .name("Cloud Architect Portfolio")
            .sku("PORT-999")
            .currency("EUR")
            .price(49.5)
            .stockQuantity(15);
        Product productDto = new Product().id(entity.getId()).name(entity.getName()).sku(entity.getSku());

        when(productRepository.findBySkuIgnoreCase("PORT-999")).thenReturn(Optional.empty());
        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toProduct(entity)).thenReturn(productDto);

        Product result = productService.createProduct(request);

        assertThat(result).isSameAs(productDto);
        verify(productRepository).save(entity);
        verify(productEventPublisher).publishUpsert(entity);
        verify(catalogMailService).sendNewProductNotification(entity.getName(), entity.getSku());
    }

    @Test
    @DisplayName("createProduct should throw ConflictException when sku already exists")
    void createProductWithDuplicateSkuThrowsConflict() {
        CreateProductRequest request = new CreateProductRequest().sku("PORT-999");
        when(productRepository.findBySkuIgnoreCase("PORT-999")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("already exists");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateAvailability should change status and reindex document")
    void updateAvailabilityChangesStatus() {
        ProductAvailabilityRequest request = new ProductAvailabilityRequest()
            .status(ProductAvailabilityRequest.StatusEnum.INACTIVE);
        when(productRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(entity);
        Product expected = new Product().id(entity.getId());
        when(productMapper.toProduct(entity)).thenReturn(expected);

        Product result = productService.updateAvailability(entity.getId(), request);

        assertThat(entity.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(result).isSameAs(expected);
        verify(productEventPublisher).publishUpsert(entity);
    }

    @Test
    @DisplayName("updateProduct should persist changes, check sku uniqueness and publish event")
    void updateProductPersistsChanges() {
        UpdateProductRequest request = new UpdateProductRequest()
            .name("Updated Product")
            .sku("PORT-123");
        ProductEntity saved = entity;
        Product dto = new Product().id(entity.getId()).name("Updated Product");

        when(productRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(productRepository.findBySkuIgnoreCase("PORT-123")).thenReturn(Optional.empty());
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toProduct(saved)).thenReturn(dto);

        Product result = productService.updateProduct(entity.getId(), request);

        assertThat(result).isSameAs(dto);
        verify(productMapper).updateEntity(request, entity);
        verify(productEventPublisher).publishUpsert(saved);
    }

    @Test
    @DisplayName("updateProduct should throw ConflictException when sku belongs to another product")
    void updateProductDuplicateSkuThrows() {
        UpdateProductRequest request = new UpdateProductRequest().sku("PORT-555");
        ProductEntity other = ProductEntity.builder().id(UUID.randomUUID()).sku("PORT-555").build();

        when(productRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(productRepository.findBySkuIgnoreCase("PORT-555")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> productService.updateProduct(entity.getId(), request))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("PORT-555");

        verify(productRepository, never()).save(any());
        verify(productEventPublisher, never()).publishUpsert(any());
    }

    @Test
    @DisplayName("search should return fallback results when Elasticsearch is empty")
    void searchFallsBackToRepositoryWhenIndexEmpty() {
        when(productSearchRepository
            .findTop20ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("portfolio", "portfolio"))
            .thenReturn(Collections.emptyList());
        ProductEntity recent = entity;
        when(productRepository.findTop10ByStatusOrderByLastUpdatedAtDesc(ProductStatus.ACTIVE))
            .thenReturn(List.of(recent));
        ProductSummary summary = new ProductSummary()
            .id(recent.getId())
            .name(recent.getName())
            .status(recent.getStatus().name())
            .sku(recent.getSku());
        when(productMapper.toSummary(recent)).thenReturn(summary);

        List<ProductSummary> result = productService.search("portfolio", 5);

        assertThat(result).containsExactly(summary);
    }

    @Test
    @DisplayName("search should map documents when Elasticsearch returns matches")
    void searchReturnsIndexedDocuments() {
        UUID id = UUID.randomUUID();
        ProductDocument document = ProductDocument.builder()
            .id(id)
            .name("Portfolio API")
            .sku("API-001")
            .status(ProductStatus.ACTIVE.name())
            .price(BigDecimal.TEN)
            .currency("EUR")
            .build();
        when(productSearchRepository
            .findTop20ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("api", "api"))
            .thenReturn(List.of(document));

        List<ProductSummary> result = productService.search("api", 10);

        assertThat(result)
            .extracting(ProductSummary::getId)
            .containsExactly(id);
    }

    @Test
    @DisplayName("deleteProduct should remove entity and index document")
    void deleteProductRemovesEntity() {
        when(productRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        productService.deleteProduct(entity.getId());

        verify(productRepository).delete(entity);
        verify(productEventPublisher).publishDelete(entity.getId());
    }

    @Test
    @DisplayName("deleteProduct should throw when entity is missing")
    void deleteProductNotFoundThrows() {
        when(productRepository.findById(entity.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(entity.getId()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("listProducts returns paginated result")
    void listProductsReturnsPage() {
        Product productDto = new Product().id(entity.getId());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(entity)));
        when(productMapper.toProduct(entity)).thenReturn(productDto);

        ProductPage page = productService.listProducts(0, 5, Sort.unsorted());

        assertThat(page.getContent()).containsExactly(productDto);
    }
}
