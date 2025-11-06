package com.portfolio.catalog.jobs;

import com.portfolio.catalog.entity.ProductStatus;
import com.portfolio.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogJobs {

    private static final Logger log = LoggerFactory.getLogger(CatalogJobs.class);

    private final ProductRepository productRepository;

    /**
     * Simple job that logs how many active products are currently published.
     * Helps demonstrate scheduled tasks in interviews.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void logActiveCatalogMetrics() {
        long activeProducts = productRepository.count((root, query, cb) ->
            cb.equal(root.get("status"), ProductStatus.ACTIVE));
        log.info("Catalog health check - active products: {}", activeProducts);
    }
}
