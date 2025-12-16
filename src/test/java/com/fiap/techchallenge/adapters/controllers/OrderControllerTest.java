package com.fiap.techchallenge.adapters.controllers;

import com.fiap.techchallenge.application.usecases.OrderUseCase;
import com.fiap.techchallenge.application.usecases.OrderUseCase.OrderItemRequest;
import com.fiap.techchallenge.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderUseCase orderUseCase;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private OrderItemRequest orderItemRequest;

    @BeforeEach
    void setUp() {
        UUID productId = UUID.randomUUID();
        orderItemRequest = new OrderItemRequest(productId, 2);

        Category category = new Category(UUID.randomUUID(), "Test Category");
        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(10.00))
                .category(category)
                .active(true)
                .build();

        List<OrderItem> items = List.of(OrderItem.create(product, 2));
        order = new Order(1L, "12345678900", items, BigDecimal.valueOf(20.00), 
                         OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 
                         123L, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create order successfully")
    void testShouldCreateOrderSuccessfully() {
        // Arrange
        String cpf = "12345678900";
        List<OrderItemRequest> items = List.of(orderItemRequest);
        when(orderUseCase.createOrder(cpf, items)).thenReturn(order);

        // Act
        Order result = orderController.createOrder(cpf, items);

        // Assert
        assertEquals(order, result);
        verify(orderUseCase).createOrder(cpf, items);
    }

    @Test
    @DisplayName("Should find order by id successfully")
    void testShouldFindOrderByIdSuccessfully() {
        // Arrange
        Long orderId = 1L;
        when(orderUseCase.findOrderById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderController.findOrderById(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderUseCase).findOrderById(orderId);
    }

    @Test
    @DisplayName("Should find orders by status successfully")
    void testShouldFindOrdersByStatusSuccessfully() {
        // Arrange
        OrderStatus status = OrderStatus.RECEIVED;
        List<Order> orders = List.of(order);
        when(orderUseCase.findByOptionalStatus(status)).thenReturn(orders);

        // Act
        List<Order> result = orderController.findByOptionalStatus(status);

        // Assert
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderUseCase).findByOptionalStatus(status);
    }

    @Test
    @DisplayName("Should update order status successfully")
    void testShouldUpdateOrderStatusSuccessfully() {
        // Arrange
        Long orderId = 1L;
        OrderStatus newStatus = OrderStatus.IN_PREPARATION;
        when(orderUseCase.updateOrderStatus(orderId, newStatus)).thenReturn(order);

        // Act
        Order result = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertEquals(order, result);
        verify(orderUseCase).updateOrderStatus(orderId, newStatus);
    }

    @Test
    @DisplayName("Should update order status to preparation successfully")
    void testShouldUpdateOrderStatusToPreparationSuccessfully() {
        // Arrange
        Long orderId = 1L;
        when(orderUseCase.updateOrderStatus(orderId)).thenReturn(order);

        // Act
        Order result = orderController.updateOrderStatus(orderId);

        // Assert
        assertEquals(order, result);
        verify(orderUseCase).updateOrderStatus(orderId);
    }

    @Test
    @DisplayName("Should update order payment status successfully")
    void testShouldUpdateOrderPaymentStatusSuccessfully() {
        // Arrange
        Long orderId = 1L;
        StatusPayment newStatus = StatusPayment.APROVADO;
        when(orderUseCase.updateOrderStatusPayment(orderId, newStatus)).thenReturn(order);

        // Act
        Order result = orderController.updateOrderStatusPayment(orderId, newStatus);

        // Assert
        assertEquals(order, result);
        verify(orderUseCase).updateOrderStatusPayment(orderId, newStatus);
    }
}