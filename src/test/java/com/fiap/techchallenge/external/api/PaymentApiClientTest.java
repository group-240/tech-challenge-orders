package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentApiClientTest {

    @Test
    @DisplayName("Should create PaymentApiClient with base URL")
    void testShouldCreatePaymentApiClientWithBaseUrl() {
        // Act
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl());

        // Assert
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should throw exception when API call fails with invalid host")
    void testShouldThrowExceptionWhenApiCallFails() {
        // Arrange - usar URL inválida para forçar erro
        PaymentApiClient client = new PaymentApiClient(TestConfig.INVALID_URL);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test payment", "pix", 1,
                    TestConfig.TEST_EMAIL, "CPF", TestConfig.TEST_CPF);
        });

        // Verifica que a exceção contém a mensagem esperada
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should throw exception with invalid URL format")
    void testShouldThrowExceptionWithInvalidUrlFormat() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.INVALID_URL);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test", "pix", 1,
                    TestConfig.TEST_EMAIL, "CPF", TestConfig.TEST_CPF);
        });
        
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle null values in createPayment")
    void testShouldHandleNullValuesInCreatePayment() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl());

        // Act & Assert - null values will cause issues in String.format
        assertThrows(Exception.class, () -> {
            client.createPayment(null, null, null, null, null, null, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when connection fails (connection refused)")
    void testShouldThrowExceptionWhenConnectionRefused() {
        // Arrange - porta que provavelmente não está em uso
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl(TestConfig.PAYMENT_API_PORT_INVALID));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(100.0, "Test", "pix", 1,
                    TestConfig.TEST_EMAIL, "CPF", TestConfig.TEST_CPF);
        });

        // Verifica que o erro foi encapsulado corretamente
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("Should format JSON payload correctly")
    void testShouldFormatJsonPayloadCorrectly() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl(TestConfig.PAYMENT_API_PORT_NULL_VALUES));

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
    void testShouldHandleSpecialCharactersInDescription() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl(TestConfig.PAYMENT_API_PORT_JSON_FORMAT));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(
                    100.0,
                    "Teste com caracteres especiais",
                    "pix",
                    1,
                    TestConfig.TEST_EMAIL,
                    "CPF",
                    TestConfig.TEST_CPF
            );
        });

        // Verifica que o erro é de conexão, não de formatação
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle different installment values")
    void testShouldHandleDifferentInstallmentValues() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl(TestConfig.PAYMENT_API_PORT_SPECIAL_CHARS));

        // Act & Assert - Testa com múltiplas parcelas
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(1200.0, "Test", "credit_card", 12,
                    TestConfig.TEST_EMAIL, "CPF", TestConfig.TEST_CPF);
        });
        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }

    @Test
    @DisplayName("Should handle different amount formats")
    void testShouldHandleDifferentAmountFormats() {
        // Arrange
        PaymentApiClient client = new PaymentApiClient(TestConfig.getPaymentApiUrl(TestConfig.PAYMENT_API_PORT_INSTALLMENTS));

        // Act & Assert - Testa valores decimais
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.createPayment(
                    99.99,
                    "Test decimal",
                    "pix",
                    1,
                    TestConfig.TEST_EMAIL,
                    "CPF",
                    TestConfig.TEST_CPF
            );
        });

        assertTrue(exception.getMessage().contains("Erro ao chamar API de pagamento"));
    }
}
