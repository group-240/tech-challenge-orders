package com.fiap.techchallenge.external.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentApiClientTest {

    @Test
    @DisplayName("Should create PaymentApiClient with base URL")
    void shouldCreatePaymentApiClientWithBaseUrl() {
        // Act
        PaymentApiClient client = new PaymentApiClient("http://localhost:9090");

        // Assert
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should throw exception when API call fails")
    void shouldThrowExceptionWhenApiCallFails() {
        // Arrange - usar URL inválida para forçar erro
        PaymentApiClient client = new PaymentApiClient("http://invalid-url-that-does-not-exist-12345");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test payment", "pix", 1,
                    "test@test.com", "CPF", "12345678900");
        });
    }

    @Test
    @DisplayName("Should throw exception with invalid URL format")
    void shouldThrowExceptionWithInvalidUrlFormat() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("invalid-url");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test", "pix", 1,
                    "test@test.com", "CPF", "12345678900");
        });
        
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle null values in createPayment")
    void shouldHandleNullValuesInCreatePayment() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("http://localhost:9090");

        // Act & Assert - null values will cause issues in String.format
        assertThrows(Exception.class, () -> {
            client.createPayment(null, null, null, null, null, null, null);
        });
    }
}
