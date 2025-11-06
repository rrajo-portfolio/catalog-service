package com.portfolio.catalog;

import com.portfolio.catalog.config.SearchTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(SearchTestConfig.class)
class CatalogServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
