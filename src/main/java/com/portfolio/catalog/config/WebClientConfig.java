package com.portfolio.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient usersServiceWebClient(
        @Value("${services.users.base-url}") String baseUrl,
        WebClient.Builder builder
    ) {
        return builder
            .baseUrl(baseUrl)
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(256 * 1024))
                .build())
            .build();
    }
}
