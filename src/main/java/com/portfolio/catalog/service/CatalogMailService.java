package com.portfolio.catalog.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogMailService {

    private static final Logger log = LoggerFactory.getLogger(CatalogMailService.class);

    private final JavaMailSender mailSender;

    @Value("${notifications.catalog.to}")
    private String notificationRecipient;

    public void sendNewProductNotification(String productName, String sku) {
        if (notificationRecipient == null || notificationRecipient.isBlank()) {
            log.debug("Skipping email notification because notifications.catalog.to is not configured");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationRecipient);
        message.setSubject("Nuevo producto publicado");
        message.setText("Se registró el producto %s con SKU %s".formatted(productName, sku));
        mailSender.send(message);
    }
}
