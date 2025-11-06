package com.portfolio.catalog.controller;

import com.portfolio.catalog.generated.api.ProductsApi;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.ProductAvailabilityRequest;
import com.portfolio.catalog.generated.model.ProductPage;
import com.portfolio.catalog.generated.model.ProductSummary;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import com.portfolio.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductsController implements ProductsApi {

    private final ProductService productService;

    @Override
    @PreAuthorize("hasRole('catalog_admin') or hasRole('portfolio_admin')")
    public ResponseEntity<Product> createProduct(CreateProductRequest createProductRequest) {
        return ResponseEntity.status(201).body(productService.createProduct(createProductRequest));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.read','ROLE_catalog_read','ROLE_catalog-read','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<Product> getProduct(UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.write','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<Product> updateProduct(UUID id, UpdateProductRequest updateProductRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, updateProductRequest));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.write','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<Void> deleteProduct(UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.read','ROLE_catalog_read','ROLE_catalog-read','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<ProductPage> listProducts(Integer page, Integer size, String sort) {
        ProductPage result = productService.listProducts(page, size, productService.resolveSort(sort));
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.write','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<Product> patchAvailability(UUID id, ProductAvailabilityRequest productAvailabilityRequest) {
        return ResponseEntity.ok(productService.updateAvailability(id, productAvailabilityRequest));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_catalog.read','ROLE_catalog_read','ROLE_catalog-read','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<List<ProductSummary>> searchProducts(String q, Integer limit) {
        return ResponseEntity.ok(productService.search(q, limit));
    }
}
