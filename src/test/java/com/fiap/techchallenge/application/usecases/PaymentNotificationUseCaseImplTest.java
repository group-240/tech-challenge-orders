package com.fiap.techchallenge.application.usecases;

import com.fiap.techchallenge.domain.entities.StatusPayment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationUseCaseImplTest {

    @Mock
    private OrderUseCase orderUseCase;

    @InjectMocks
    private PaymentNotificationUseCaseImpl paymentNotificationUseCase;

    @Test
    @DisplayName("Should handle payment notification successfully")
    void testShouldHandlePaymentNotificationSuccessfully() {
        // Arrange
        Long paymentId = 123L;

        // Act
        paymentNotificationUseCase.handlePaymentNotification(paymentId);

        // Assert
        verify(orderUseCase).updateOrderStatusPayment(paymentId, StatusPayment.APROVADO);
    }

    @Test
    @DisplayName("Should handle exception silently during payment notification")
    void testShouldHandleExceptionSilentlyDuringPaymentNotification() {
        // Arrange
        Long paymentId = 123L;
        doThrow(new RuntimeException("Order not found"))
            .when(orderUseCase).updateOrderStatusPayment(paymentId, StatusPayment.APROVADO);

        // Act - should not throw exception
        paymentNotificationUseCase.handlePaymentNotification(paymentId);

        // Assert
        verify(orderUseCase).updateOrderStatusPayment(paymentId, StatusPayment.APROVADO);
    }
}
