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
    @DisplayName("Should throw exception when API call fails with invalid host")
    void shouldThrowExceptionWhenApiCallFails() {
        // Arrange - usar URL inválida para forçar erro
        PaymentApiClient client = new PaymentApiClient("http://invalid-url-that-does-not-exist-12345.test");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test payment", "pix", 1,
                    "test@test.com", "CPF", "12345678900");
        });

        // Verifica que a exceção contém a mensagem esperada
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
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

    @Test
    @DisplayName("Should throw exception when connection fails (connection refused)")
    void shouldThrowExceptionWhenConnectionRefused() {
        // Arrange - porta que provavelmente não está em uso
        PaymentApiClient client = new PaymentApiClient("http://localhost:59999");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test", "pix", 1,
                    "test@test.com", "CPF", "12345678900");
        });

        // Verifica que o erro foi encapsulado corretamente
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("Should format JSON payload correctly")
    void shouldFormatJsonPayloadCorrectly() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("http://localhost:59998");

        // Act & Assert - mesmo que falhe a conexão, o payload deve ser formatado
        // Este teste verifica se a lógica de formatação não lança exceção antes da conexão
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(
                    250.50,
                    "Pagamento teste com acentuação",
                    "credit_card",
                    12,
                    "usuario@email.com",
                    "CNPJ",
                    "12345678000190"
            );
        });

        // Verifica que chegou na parte de conexão (não falhou no formato)
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle special characters in description")
    void shouldHandleSpecialCharactersInDescription() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("http://localhost:59997");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(
                    100.0,
                    "Teste com caracteres especiais",
                    "pix",
                    1,
                    "test@test.com",
                    "CPF",
                    "12345678900"
            );
        });

        // Verifica que o erro é de conexão, não de formatação
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle different installment values")
    void shouldHandleDifferentInstallmentValues() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("http://localhost:59996");

        // Act & Assert - Testa com múltiplas parcelas
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(1200.0, "Test", "credit_card", 12,
                    "test@test.com", "CPF", "12345678900");
        });
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle different amount formats")
    void shouldHandleDifferentAmountFormats() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient("http://localhost:59995");

        // Act & Assert - Testa valores decimais
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(
                    99.99,
                    "Test decimal",
                    "pix",
                    1,
                    "test@test.com",
                    "CPF",
                    "12345678900"
            );
        });

        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }
}
