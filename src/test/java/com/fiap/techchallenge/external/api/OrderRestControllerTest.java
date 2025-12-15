package com.fiap.techchallenge.external.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.adapters.controllers.OrderController;
import com.fiap.techchallenge.application.usecases.OrderUseCase.OrderItemRequest;
import com.fiap.techchallenge.domain.entities.Order;
import com.fiap.techchallenge.domain.entities.OrderStatus;
import com.fiap.techchallenge.domain.entities.StatusPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderRestControllerTest {

    @Mock
    private OrderController orderController;

    @InjectMocks
    private OrderRestController orderRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder_Success() throws Exception {
        // Arrange
        OrderRestController.OrderRequestDTO requestDTO = new OrderRestController.OrderRequestDTO();
        requestDTO.setCpf("12345678901");
        
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(2);
        requestDTO.setItems(Arrays.asList(item));

        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus(OrderStatus.RECEIVED);

        when(orderController.createOrder(eq("12345678901"), anyList())).thenReturn(mockOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void findOrderById_Success() throws Exception {
        // Arrange
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus(OrderStatus.IN_PREPARATION);

        when(orderController.findOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"));
    }

    @Test
    void findOrderById_NotFound() throws Exception {
        // Arrange
        when(orderController.findOrderById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByOptionalStatus_WithStatus() throws Exception {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.RECEIVED);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.RECEIVED);

        List<Order> mockOrders = Arrays.asList(order1, order2);

        when(orderController.findByOptionalStatus(OrderStatus.RECEIVED)).thenReturn(mockOrders);

        // Act & Assert
        mockMvc.perform(get("/orders")
                .param("status", "RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void findByOptionalStatus_WithoutStatus() throws Exception {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.RECEIVED);

        List<Order> mockOrders = Arrays.asList(order1);

        when(orderController.findByOptionalStatus(null)).thenReturn(mockOrders);

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        // Arrange
        OrderRestController.OrderStatusUpdateDTO updateDTO = new OrderRestController.OrderStatusUpdateDTO();
        updateDTO.setStatus(OrderStatus.READY);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.READY);

        when(orderController.updateOrderStatus(1L, OrderStatus.READY)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    void updateOrderStatusToPreparation_Success() throws Exception {
        // Arrange
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.IN_PREPARATION);

        when(orderController.updateOrderStatus(1L)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/orders/1/status/preparation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"));
    }

    @Test
    void updateOrderStatusPayment_Success() throws Exception {
        // Arrange
        OrderRestController.PaymentStatusUpdateDTO paymentUpdateDTO = new OrderRestController.PaymentStatusUpdateDTO();
        paymentUpdateDTO.setStatusPayment(StatusPayment.APROVADO);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatusPayment(StatusPayment.APROVADO);

        when(orderController.updateOrderStatusPayment(1L, StatusPayment.APROVADO)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/orders/1/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statusPayment").value("APROVADO"));
    }

    @Test
    void testOrderRequestDTO_GettersAndSetters() {
        // Arrange
        OrderRestController.OrderRequestDTO dto = new OrderRestController.OrderRequestDTO();
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(1);
        List<OrderItemRequest> items = Arrays.asList(item);

        // Act
        dto.setCpf("12345678901");
        dto.setItems(items);

        // Assert
        assertEquals("12345678901", dto.getCpf());
        assertEquals(items, dto.getItems());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testOrderStatusUpdateDTO_GettersAndSetters() {
        // Arrange
        OrderRestController.OrderStatusUpdateDTO dto = new OrderRestController.OrderStatusUpdateDTO();

        // Act
        dto.setStatus(OrderStatus.FINISHED);

        // Assert
        assertEquals(OrderStatus.FINISHED, dto.getStatus());
    }

    @Test
    void testPaymentStatusUpdateDTO_GettersAndSetters() {
        // Arrange
        OrderRestController.PaymentStatusUpdateDTO dto = new OrderRestController.PaymentStatusUpdateDTO();

        // Act
        dto.setStatusPayment(StatusPayment.REJEITADO);

        // Assert
        assertEquals(StatusPayment.REJEITADO, dto.getStatusPayment());
    }
}