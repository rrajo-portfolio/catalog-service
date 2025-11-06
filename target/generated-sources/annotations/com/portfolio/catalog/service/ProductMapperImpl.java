package com.portfolio.catalog.service;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.generated.model.CreateProductRequest;
import com.portfolio.catalog.generated.model.Product;
import com.portfolio.catalog.generated.model.ProductSummary;
import com.portfolio.catalog.generated.model.UpdateProductRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-06T08:24:47+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductEntity toEntity(CreateProductRequest request) {
        if ( request == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.name( request.getName() );
        productEntity.description( request.getDescription() );
        productEntity.sku( request.getSku() );
        if ( request.getPrice() != null ) {
            productEntity.price( BigDecimal.valueOf( request.getPrice() ) );
        }
        productEntity.currency( request.getCurrency() );
        productEntity.stockQuantity( request.getStockQuantity() );
        List<String> list = request.getTags();
        if ( list != null ) {
            productEntity.tags( new ArrayList<String>( list ) );
        }

        productEntity.id( UUID.randomUUID() );
        productEntity.status( ProductStatus.ACTIVE );
        productEntity.lastUpdatedAt( OffsetDateTime.now() );

        return productEntity.build();
    }

    @Override
    public void updateEntity(UpdateProductRequest request, ProductEntity entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getSku() != null ) {
            entity.setSku( request.getSku() );
        }
        if ( request.getPrice() != null ) {
            entity.setPrice( BigDecimal.valueOf( request.getPrice() ) );
        }
        if ( request.getCurrency() != null ) {
            entity.setCurrency( request.getCurrency() );
        }
        if ( request.getStockQuantity() != null ) {
            entity.setStockQuantity( request.getStockQuantity() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( statusEnumToProductStatus( request.getStatus() ) );
        }
        if ( entity.getTags() != null ) {
            List<String> list = request.getTags();
            if ( list != null ) {
                entity.getTags().clear();
                entity.getTags().addAll( list );
            }
        }
        else {
            List<String> list = request.getTags();
            if ( list != null ) {
                entity.setTags( new ArrayList<String>( list ) );
            }
        }

        touchTimestamp( entity );
    }

    @Override
    public Product toProduct(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Product product = new Product();

        product.setId( entity.getId() );
        product.setName( entity.getName() );
        product.setDescription( entity.getDescription() );
        product.setSku( entity.getSku() );
        if ( entity.getPrice() != null ) {
            product.setPrice( entity.getPrice().doubleValue() );
        }
        product.setCurrency( entity.getCurrency() );
        product.setStockQuantity( entity.getStockQuantity() );
        product.setStatus( productStatusToStatusEnum( entity.getStatus() ) );
        List<String> list = entity.getTags();
        if ( list != null ) {
            product.setTags( new ArrayList<String>( list ) );
        }
        product.setLastUpdatedAt( entity.getLastUpdatedAt() );

        return product;
    }

    @Override
    public ProductSummary toSummary(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductSummary productSummary = new ProductSummary();

        productSummary.setId( entity.getId() );
        productSummary.setName( entity.getName() );
        productSummary.setSku( entity.getSku() );
        if ( entity.getStatus() != null ) {
            productSummary.setStatus( entity.getStatus().name() );
        }

        return productSummary;
    }

    protected ProductStatus statusEnumToProductStatus(UpdateProductRequest.StatusEnum statusEnum) {
        if ( statusEnum == null ) {
            return null;
        }

        ProductStatus productStatus;

        switch ( statusEnum ) {
            case ACTIVE: productStatus = ProductStatus.ACTIVE;
            break;
            case INACTIVE: productStatus = ProductStatus.INACTIVE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + statusEnum );
        }

        return productStatus;
    }

    protected Product.StatusEnum productStatusToStatusEnum(ProductStatus productStatus) {
        if ( productStatus == null ) {
            return null;
        }

        Product.StatusEnum statusEnum;

        switch ( productStatus ) {
            case ACTIVE: statusEnum = Product.StatusEnum.ACTIVE;
            break;
            case INACTIVE: statusEnum = Product.StatusEnum.INACTIVE;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + productStatus );
        }

        return statusEnum;
    }
}
