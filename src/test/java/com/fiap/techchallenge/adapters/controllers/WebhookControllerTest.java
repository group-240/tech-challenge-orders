package com.fiap.techchallenge.adapters.controllers;

import com.fiap.techchallenge.application.usecases.PaymentNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private PaymentNotificationUseCase paymentNotificationUseCase;

    @InjectMocks
    private WebhookController webhookController;

    @Test
    @DisplayName("Should handle payment notification successfully")
    void testShouldHandlePaymentNotificationSuccessfully() {
        // Arrange
        Long paymentId = 123L;

        // Act
        webhookController.handlePaymentNotification(paymentId);

        // Assert
        verify(paymentNotificationUseCase).handlePaymentNotification(paymentId);
    }

    @Test
    @DisplayName("Should handle null payment ID gracefully")
    void testShouldHandleNullPaymentIdGracefully() {
        // Arrange
        Long paymentId = null;

        // Act
        webhookController.handlePaymentNotification(paymentId);

        // Assert
        verifyNoInteractions(paymentNotificationUseCase);
    }
}