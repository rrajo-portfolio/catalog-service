package com.portfolio.catalog.config;

import com.portfolio.catalog.search.ProductSearchRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class SearchTestConfig {

    @Bean
    public ProductSearchRepository productSearchRepository() {
        return Mockito.mock(ProductSearchRepository.class);
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }
}
