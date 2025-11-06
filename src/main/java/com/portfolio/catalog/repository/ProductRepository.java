package com.portfolio.catalog.repository;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findBySkuIgnoreCase(String sku);

    List<ProductEntity> findTop10ByStatusOrderByLastUpdatedAtDesc(ProductStatus status);
}
