package com.fiap.techchallenge.adapters.controllers;

import com.fiap.techchallenge.application.usecases.PaymentUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentUseCase paymentUseCase;

    @InjectMocks
    private PaymentController paymentController;

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
        
        when(paymentUseCase.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber))
            .thenReturn(expectedResponse);

        // Act
        String result = paymentController.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);

        // Assert
        assertEquals(expectedResponse, result);
        verify(paymentUseCase).createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);
    }
}