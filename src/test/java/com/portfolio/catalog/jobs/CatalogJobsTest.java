package com.portfolio.catalog.jobs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.catalog.entity.ProductEntity;
import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.repository.ProductRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CatalogJobsTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    void logActiveCatalogMetricsCountsActiveProducts() {
        CatalogJobs jobs = new CatalogJobs(productRepository);
        ArgumentCaptor<Specification<ProductEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(productRepository.count(org.mockito.ArgumentMatchers.<Specification<ProductEntity>>any())).thenReturn(5L);

        jobs.logActiveCatalogMetrics();

        verify(productRepository).count(specCaptor.capture());
        Specification<ProductEntity> specification = specCaptor.getValue();
        Root<ProductEntity> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<ProductEntity> query = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Path<Object> path = (Path<Object>) mock(Path.class);
        when(root.get("status")).thenReturn(path);

        specification.toPredicate(root, query, cb);

        verify(cb).equal(path, ProductStatus.ACTIVE);
    }
}
