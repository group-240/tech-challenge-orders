package com.fiap.techchallenge.external.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.adapters.controllers.WebhookController;
import com.fiap.techchallenge.external.api.dto.Data;
import com.fiap.techchallenge.external.api.dto.WebhookRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebhookRestControllerTest {

    @Mock
    private WebhookController webhookController;

    @InjectMocks
    private WebhookRestController webhookRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webhookRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void handleNotification_Success() throws Exception {
        // Arrange
        WebhookRequestDTO webhookRequest = new WebhookRequestDTO();
        webhookRequest.setId(123L);
        webhookRequest.setLiveMode(true);
        webhookRequest.setType("payment");
        webhookRequest.setAction("payment.created");
        webhookRequest.setDateCreated("2025-08-01T19:40:00Z");
        webhookRequest.setUserId(987654321L);
        webhookRequest.setApiVersion("v1");

        Data data = new Data();
        data.setId(9988776655L);
        webhookRequest.setData(data);

        doNothing().when(webhookController).handlePaymentNotification(eq(9988776655L));

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webhookRequest)))
                .andExpect(status().isOk());

        // Verify that the webhook controller was called with the correct payment ID
        verify(webhookController).handlePaymentNotification(eq(9988776655L));
    }

    @Test
    void handleNotification_WithMinimalData() throws Exception {
        // Arrange
        WebhookRequestDTO webhookRequest = new WebhookRequestDTO();
        Data data = new Data();
        data.setId(1234567890L);
        webhookRequest.setData(data);

        doNothing().when(webhookController).handlePaymentNotification(eq(1234567890L));

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webhookRequest)))
                .andExpect(status().isOk());

        // Verify that the webhook controller was called with the correct payment ID
        verify(webhookController).handlePaymentNotification(eq(1234567890L));
    }

    @Test
    void handleNotification_WithDifferentPaymentId() throws Exception {
        // Arrange
        WebhookRequestDTO webhookRequest = new WebhookRequestDTO();
        webhookRequest.setId(999L);
        webhookRequest.setType("payment");
        webhookRequest.setAction("payment.updated");

        Data data = new Data();
        data.setId(555666777L);
        webhookRequest.setData(data);

        doNothing().when(webhookController).handlePaymentNotification(eq(555666777L));

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webhookRequest)))
                .andExpect(status().isOk());

        // Verify that the webhook controller was called with the correct payment ID
        verify(webhookController).handlePaymentNotification(eq(555666777L));
    }

    @Test
    void handleNotification_CompletePayload() throws Exception {
        // Arrange
        WebhookRequestDTO webhookRequest = new WebhookRequestDTO();
        webhookRequest.setId(100L);
        webhookRequest.setLiveMode(false);
        webhookRequest.setType("payment");
        webhookRequest.setAction("payment.approved");
        webhookRequest.setDateCreated("2025-11-04T12:00:00Z");
        webhookRequest.setUserId(12345L);
        webhookRequest.setApiVersion("v2");

        Data data = new Data();
        data.setId(7777888899L);
        webhookRequest.setData(data);

        doNothing().when(webhookController).handlePaymentNotification(eq(7777888899L));

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webhookRequest)))
                .andExpect(status().isOk());

        // Verify that the webhook controller was called with the correct payment ID
        verify(webhookController).handlePaymentNotification(eq(7777888899L));
    }
}