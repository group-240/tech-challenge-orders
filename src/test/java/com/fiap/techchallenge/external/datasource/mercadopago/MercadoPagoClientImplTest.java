package com.fiap.techchallenge.external.datasource.mercadopago;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoPagoClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MercadoPagoClientImpl mercadoPagoClient;

    private String mockAccessToken = "TEST-123456";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mercadoPagoClient, "accessToken", mockAccessToken);
        ReflectionTestUtils.setField(mercadoPagoClient, "restTemplate", restTemplate);
    }

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
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = mercadoPagoClient.createPaymentOrder(
                amount, description, paymentMethodId, installments, 
                payerEmail, identificationType, identificationNumber);

        // Assert
        assertEquals(expectedResponse, result);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Should handle exception during payment creation")
    void shouldHandleExceptionDuringPaymentCreation() {
        // Arrange
        Double amount = 100.0;
        String description = "Test payment";
        String paymentMethodId = "pix";
        Integer installments = 1;
        String payerEmail = "test@test.com";
        String identificationType = "CPF";
        String identificationNumber = "12345678900";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        // Act
        String result = mercadoPagoClient.createPaymentOrder(
                amount, description, paymentMethodId, installments, 
                payerEmail, identificationType, identificationNumber);

        // Assert
        assertTrue(result.startsWith("Erro ao criar pagamento:"));
        assertTrue(result.contains("Network error"));
    }

    @Test
    @DisplayName("Should create payment order with minimal parameters")
    void shouldCreatePaymentOrderWithMinimalParameters() {
        // Arrange
        Double amount = 50.0;
        String description = "Minimal payment";
        String paymentMethodId = "credit_card";
        Integer installments = 2;
        String payerEmail = "minimal@test.com";

        String expectedResponse = "{\"id\":456,\"status\":\"pending\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = mercadoPagoClient.createPaymentOrder(
                amount, description, paymentMethodId, installments, 
                payerEmail, null, null);

        // Assert
        assertEquals(expectedResponse, result);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
}