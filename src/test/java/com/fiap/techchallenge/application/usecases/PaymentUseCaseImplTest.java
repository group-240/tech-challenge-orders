package com.fiap.techchallenge.application.usecases;

import com.fiap.techchallenge.domain.repositories.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentUseCaseImpl paymentUseCase;

    @Test
    @DisplayName("Should create payment order successfully")
    void shouldCreatePaymentOrderSuccessfully() {
        // Arrange
        Double amount = 100.0;
        String description = "Test payment";
        String paymentMethodId = "pix";
        Integer installments = 1;
        String payerEmail = "test@test.com";
        String identificationType = "CPF";
        String identificationNumber = "12345678900";
        
        String expectedResponse = "{\"id\":123,\"status\":\"pending\"}";
        
        when(paymentRepository.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber))
            .thenReturn(expectedResponse);

        // Act
        String result = paymentUseCase.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);

        // Assert
        assertEquals(expectedResponse, result);
        verify(paymentRepository).createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);
    }

    @Test
    @DisplayName("Should handle payment repository exception")
    void shouldHandlePaymentRepositoryException() {
        // Arrange
        Double amount = 100.0;
        String description = "Test payment";
        String paymentMethodId = "pix";
        Integer installments = 1;
        String payerEmail = "test@test.com";
        String identificationType = "CPF";
        String identificationNumber = "12345678900";

        when(paymentRepository.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber))
            .thenThrow(new RuntimeException("Payment service error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentUseCase.createPaymentOrder(
                amount, description, paymentMethodId, installments, 
                payerEmail, identificationType, identificationNumber);
        });

        assertEquals("Payment service error", exception.getMessage());
        verify(paymentRepository).createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);
    }
}