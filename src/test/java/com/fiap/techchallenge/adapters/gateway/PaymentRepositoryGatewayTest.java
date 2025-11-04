package com.fiap.techchallenge.adapters.gateway;

import com.fiap.techchallenge.external.datasource.mercadopago.MercadoPagoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryGatewayTest {

    @Mock
    private MercadoPagoClient mercadoPagoClient;

    @InjectMocks
    private PaymentRepositoryGateway paymentRepositoryGateway;

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
        
        when(mercadoPagoClient.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber))
            .thenReturn(expectedResponse);

        // Act
        String result = paymentRepositoryGateway.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mercadoPagoClient).createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);
    }

    @Test
    @DisplayName("Should handle MercadoPago client exception")
    void shouldHandleMercadoPagoClientException() {
        // Arrange
        Double amount = 100.0;
        String description = "Test payment";
        String paymentMethodId = "pix";
        Integer installments = 1;
        String payerEmail = "test@test.com";
        String identificationType = "CPF";
        String identificationNumber = "12345678900";

        when(mercadoPagoClient.createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber))
            .thenThrow(new RuntimeException("MercadoPago service error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentRepositoryGateway.createPaymentOrder(
                amount, description, paymentMethodId, installments, 
                payerEmail, identificationType, identificationNumber);
        });

        assertEquals("MercadoPago service error", exception.getMessage());
        verify(mercadoPagoClient).createPaymentOrder(
            amount, description, paymentMethodId, installments, 
            payerEmail, identificationType, identificationNumber);
    }
}