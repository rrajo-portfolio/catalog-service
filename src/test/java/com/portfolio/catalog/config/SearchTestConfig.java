package com.portfolio.catalog.config;

import com.portfolio.catalog.search.ProductSearchRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SearchTestConfig {

    @Bean
    public ProductSearchRepository productSearchRepository() {
        return Mockito.mock(ProductSearchRepository.class);
    }
}
