package com.portfolio.catalog.config;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler defaultKafkaErrorHandler() {
        ConsumerRecordRecoverer recoverer = (consumerRecord, ex) -> log.error(
            "Kafka record from topic {} partition {} offset {} failed permanently",
            consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), ex);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));
        handler.addNotRetryableExceptions(SerializationException.class, InvalidDefinitionException.class);
        return handler;
    }
}