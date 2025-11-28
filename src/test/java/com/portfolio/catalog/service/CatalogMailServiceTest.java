package com.portfolio.catalog.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CatalogMailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private CatalogMailService catalogMailService;

    @BeforeEach
    void setUp() {
        catalogMailService = new CatalogMailService(mailSender);
    }

    @Test
    void sendNewProductNotificationSkipsWhenRecipientMissing() {
        ReflectionTestUtils.setField(catalogMailService, "notificationRecipient", " ");

        catalogMailService.sendNewProductNotification("Demo", "SKU-1");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNewProductNotificationSendsMail() {
        ReflectionTestUtils.setField(catalogMailService, "notificationRecipient", "talent@portfolio.local");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        catalogMailService.sendNewProductNotification("Serverless Catalog", "PORT-42");

        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(message.getTo()).contains("talent@portfolio.local");
        org.assertj.core.api.Assertions.assertThat(message.getText()).contains("Serverless Catalog", "PORT-42");
    }
}
