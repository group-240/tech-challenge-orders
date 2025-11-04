package com.fiap.techchallenge.external.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.adapters.controllers.PaymentController;
import com.fiap.techchallenge.external.api.dto.PaymentOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentRestControllerTest {

    @Mock
    private PaymentController paymentController;

    @InjectMocks
    private PaymentRestController paymentRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder_Success() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setDescription("Test payment");
        request.setPaymentMethodId("pix");
        request.setInstallments(1);
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");

        String mockResponse = "Payment order created successfully";

        when(paymentController.createPaymentOrder(
                eq(10.50),
                eq("Test payment"),
                eq("pix"),
                eq(1),
                eq("test@example.com"),
                eq("CPF"),
                eq("12345678901")
        )).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment order created successfully"));
    }

    @Test
    void createOrder_MissingAmount_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setDescription("Test payment");
        request.setPaymentMethodId("pix");
        request.setInstallments(1);
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");
        // Amount is missing (null)

        // Act & Assert - Spring validation will catch this before our code
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_MissingDescription_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setPaymentMethodId("pix");
        request.setInstallments(1);
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");
        // Description is missing (null)

        // Act & Assert - Spring validation will catch this before our code
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_MissingPaymentMethodId_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setDescription("Test payment");
        request.setInstallments(1);
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");
        // PaymentMethodId is missing (null)

        // Act & Assert - Spring validation will catch this before our code
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_MissingInstallments_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setDescription("Test payment");
        request.setPaymentMethodId("pix");
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");
        // Installments is missing (null)

        // Act & Assert - Spring validation will catch this before our code
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_MissingPayerEmail_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setDescription("Test payment");
        request.setPaymentMethodId("pix");
        request.setInstallments(1);
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");
        // PayerEmail is missing (null)

        // Act & Assert - Spring validation will catch this before our code
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_ControllerThrowsException_BadRequest() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(10.50);
        request.setDescription("Test payment");
        request.setPaymentMethodId("pix");
        request.setInstallments(1);
        request.setPayerEmail("test@example.com");
        request.setIdentificationType("CPF");
        request.setIdentificationNumber("12345678901");

        when(paymentController.createPaymentOrder(
                eq(10.50),
                eq("Test payment"),
                eq("pix"),
                eq(1),
                eq("test@example.com"),
                eq("CPF"),
                eq("12345678901")
        )).thenThrow(new RuntimeException("Payment service error"));

        // Act & Assert
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro na requisição: Payment service error"));
    }

    @Test
    void createOrder_WithOptionalFields() throws Exception {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest();
        request.setAmount(25.75);
        request.setDescription("Payment with optional fields");
        request.setPaymentMethodId("credit_card");
        request.setInstallments(3);
        request.setPayerEmail("optional@example.com");
        request.setIdentificationType("CPF"); // Required field
        // identificationNumber is optional and null

        String mockResponse = "Payment order created with optional fields";

        when(paymentController.createPaymentOrder(
                eq(25.75),
                eq("Payment with optional fields"),
                eq("credit_card"),
                eq(3),
                eq("optional@example.com"),
                eq("CPF"),
                isNull()
        )).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment order created with optional fields"));
    }
}