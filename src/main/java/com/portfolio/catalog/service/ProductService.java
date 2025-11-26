package com.portfolio.catalog.service;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.exception.ConflictException;
import com.portfolio.catalog.exception.ResourceNotFoundException;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.ProductAvailabilityRequest;
import com.portfolio.catalog.generated.model.ProductPage;
import com.portfolio.catalog.generated.model.ProductSummary;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import com.portfolio.catalog.events.ProductEventPublisher;
import com.portfolio.catalog.repository.ProductRepository;
import com.portfolio.catalog.search.ProductDocument;
import com.portfolio.catalog.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper mapper;
    private final CatalogMailService mailService;
    private final ProductEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public ProductPage listProducts(Integer page, Integer size, Sort sort) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size, sort);
        Page<ProductEntity> result = repository.findAll(pageable);
        List<Product> content = result.getContent().stream()
            .map(mapper::toProduct)
            .toList();
        return new ProductPage()
            .content(content)
            .page(result.getNumber())
            .size(result.getSize())
            .totalElements(result.getTotalElements())
            .totalPages(result.getTotalPages());
    }

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        repository.findBySkuIgnoreCase(request.getSku())
            .ifPresent(existing -> { throw new ConflictException("Product with SKU %s already exists".formatted(existing.getSku())); });

        ProductEntity entity = mapper.toEntity(request);
        ProductEntity saved = repository.save(entity);
        eventPublisher.publishUpsert(saved);
        mailService.sendNewProductNotification(saved.getName(), saved.getSku());
        return mapper.toProduct(saved);
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID id) {
        ProductEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(id)));
        return mapper.toProduct(entity);
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequest request) {
        ProductEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(id)));
        validateSkuConflict(id, request.getSku());
        mapper.updateEntity(request, entity);
        ProductEntity saved = repository.save(entity);
        eventPublisher.publishUpsert(saved);
        return mapper.toProduct(saved);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(id)));
        repository.delete(entity);
        eventPublisher.publishDelete(entity.getId());
    }

    @Transactional
    public Product updateAvailability(UUID id, ProductAvailabilityRequest request) {
        ProductEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(id)));
        entity.setStatus(ProductStatus.valueOf(request.getStatus().getValue()));
        ProductEntity saved = repository.save(entity);
        eventPublisher.publishUpsert(saved);
        return mapper.toProduct(saved);
    }

    private void validateSkuConflict(UUID id, String sku) {
        if (sku == null || sku.isBlank()) {
            return;
        }
        repository.findBySkuIgnoreCase(sku.trim())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new ConflictException("Product with SKU %s already exists".formatted(sku));
            });
    }

    @Transactional(readOnly = true)
    public List<ProductSummary> search(String query, Integer limit) {
        int resolvedLimit = limit == null ? 10 : Math.min(limit, 50);
        List<ProductDocument> documents = searchRepository
            .findTop20ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);

        if (documents.isEmpty()) {
            return repository.findTop10ByStatusOrderByLastUpdatedAtDesc(ProductStatus.ACTIVE).stream()
                .map(mapper::toSummary)
                .toList();
        }

        return documents.stream()
            .limit(resolvedLimit)
            .map(doc -> new ProductSummary()
                .id(doc.getId())
                .name(doc.getName())
                .sku(doc.getSku())
                .status(doc.getStatus()))
            .toList();
    }

    public Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "lastUpdatedAt");
        }
        try {
            String[] parts = sort.split(",");
            String property = parts[0];
            Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
            return Sort.by(direction, property);
        } catch (Exception ex) {
            log.warn("Invalid sort parameter '{}', using default", sort);
            return Sort.by(Sort.Direction.DESC, "lastUpdatedAt");
        }
    }

}
