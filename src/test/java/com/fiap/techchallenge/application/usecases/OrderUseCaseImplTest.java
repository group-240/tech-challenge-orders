package com.fiap.techchallenge.application.usecases;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.application.usecases.OrderUseCase.OrderItemRequest;
import com.fiap.techchallenge.domain.entities.*;
import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.repositories.OrderRepository;
import com.fiap.techchallenge.domain.repositories.PaymentRepository;
import com.fiap.techchallenge.domain.repositories.ProductRepository;
import com.fiap.techchallenge.external.api.CustomerApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerApiClient customerApiClient;

    @InjectMocks
    private OrderUseCaseImpl orderUseCase;

    private Product product;
    private OrderItemRequest orderItemRequest;
    private JsonNode customerData;
    private Order order;

    @BeforeEach
    void setUp() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Category category = new Category(categoryId, "Test Category");
        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(10.00))
                .category(category)
                .active(true)
                .build();

        orderItemRequest = new OrderItemRequest(productId, 2);

        // Mock customer data
        ObjectMapper mapper = new ObjectMapper();
        String customerJson = "{\"cpf\":\"12345678900\",\"email\":\"test@test.com\"}";
        customerData = mapper.readTree(customerJson);

        // Mock order
        List<OrderItem> items = List.of(OrderItem.create(product, 2));
        order = new Order(1L, "12345678900", items, BigDecimal.valueOf(20.00), 
                         OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 
                         123L, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        when(customerApiClient.fetchCustomerByCpf("12345678900")).thenReturn(customerData);
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(paymentRepository.createPaymentOrder(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("{\"id\":123}");
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        List<OrderItemRequest> items = List.of(orderItemRequest);

        // Act
        Order result = orderUseCase.createOrder("12345678900", items);

        // Assert
        assertNotNull(result);
        assertEquals("12345678900", result.getCpf());
        assertEquals(BigDecimal.valueOf(20.00), result.getTotalAmount());
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, result.getStatusPayment());
        assertEquals(123L, result.getIdPayment());

        verify(customerApiClient).fetchCustomerByCpf("12345678900");
        verify(productRepository).findById(any(UUID.class));
        verify(paymentRepository).createPaymentOrder(any(), any(), any(), any(), any(), any(), any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found")
    void shouldThrowNotFoundExceptionWhenProductNotFound() {
        // Arrange
        when(customerApiClient.fetchCustomerByCpf("12345678900")).thenReturn(customerData);
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        List<OrderItemRequest> items = List.of(orderItemRequest);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderUseCase.createOrder("12345678900", items);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw DomainException when product is inactive")
    void shouldThrowDomainExceptionWhenProductIsInactive() {
        // Arrange
        Product inactiveProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Inactive Product")
                .description("Description")
                .price(BigDecimal.valueOf(10.00))
                .category(new Category(UUID.randomUUID(), "Category"))
                .active(false)
                .build();

        when(customerApiClient.fetchCustomerByCpf("12345678900")).thenReturn(customerData);
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(inactiveProduct));

        List<OrderItemRequest> items = List.of(orderItemRequest);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            orderUseCase.createOrder("12345678900", items);
        });

        assertTrue(exception.getMessage().contains("Product is not active"));
    }

    @Test
    @DisplayName("Should throw DomainException for invalid quantity")
    void shouldThrowDomainExceptionForInvalidQuantity() {
        // Arrange
        OrderItemRequest invalidItem = new OrderItemRequest(UUID.randomUUID(), 0);
        when(customerApiClient.fetchCustomerByCpf("12345678900")).thenReturn(customerData);

        List<OrderItemRequest> items = List.of(invalidItem);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            orderUseCase.createOrder("12345678900", items);
        });

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should find order by ID successfully")
    void shouldFindOrderByIdSuccessfully() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderUseCase.findOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when order not found by ID")
    void shouldThrowNotFoundExceptionWhenOrderNotFoundById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderUseCase.findOrderById(1L);
        });

        assertEquals("Record not found", exception.getMessage());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find orders by status")
    void shouldFindOrdersByStatus() {
        // Arrange
        List<Order> orders = List.of(order);
        when(orderRepository.findByOptionalStatus(OrderStatus.RECEIVED)).thenReturn(orders);

        // Act
        List<Order> result = orderUseCase.findByOptionalStatus(OrderStatus.RECEIVED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository).findByOptionalStatus(OrderStatus.RECEIVED);
    }

    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatusSuccessfully() {
        // Arrange
        Order paidOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(20.00), 
                                   OrderStatus.RECEIVED, StatusPayment.APROVADO, 
                                   123L, LocalDateTime.now(), LocalDateTime.now());
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(paidOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(paidOrder);

        // Act
        Order result = orderUseCase.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw DomainException when order is not paid")
    void shouldThrowDomainExceptionWhenOrderIsNotPaid() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            orderUseCase.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);
        });

        assertEquals("The order is not paid", exception.getMessage());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should update order status to preparation")
    void shouldUpdateOrderStatusToPreparation() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderUseCase.updateOrderStatus(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order payment status by payment ID")
    void shouldUpdateOrderPaymentStatusByPaymentId() {
        // Arrange
        when(orderRepository.findByIdPayment(123L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderUseCase.updateOrderStatusPayment(123L, StatusPayment.APROVADO);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findByIdPayment(123L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should validate quantity correctly")
    void shouldValidateQuantityCorrectly() {
        // Positive case already covered in create order test
        
        // Test edge case - negative quantity
        assertThrows(DomainException.class, () -> {
            orderUseCase.validateQuantity(-1);
        });
    }

    @Test
    @DisplayName("Should validate product correctly")
    void shouldValidateProductCorrectly() {
        // Arrange
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));

        // Act
        Product result = orderUseCase.validateProduct(product.getId());

        // Assert
        assertEquals(product, result);
        verify(productRepository).findById(product.getId());
    }

    @Test
    @DisplayName("Should convert order items correctly")
    void shouldConvertOrderItemsCorrectly() {
        // Arrange
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        List<OrderItemRequest> items = List.of(orderItemRequest);

        // Act
        List<OrderItem> result = orderUseCase.validateAndConvertOrderItems(items);

        // Assert
        assertEquals(1, result.size());
        OrderItem orderItem = result.get(0);
        assertEquals(product, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(BigDecimal.valueOf(20.00), orderItem.getSubTotal());
    }

    @Test
    @DisplayName("Should create payment order and parse response")
    void shouldCreatePaymentOrderAndParseResponse() {
        // Arrange
        when(paymentRepository.createPaymentOrder(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("{\"id\":456}");

        List<OrderItem> orderItems = List.of(OrderItem.create(product, 1));
        Order testOrder = Order.create("12345678900", orderItems);

        // Act
        Long paymentId = orderUseCase.createPaymentOrder(testOrder, customerData);

        // Assert
        assertEquals(456L, paymentId);
        verify(paymentRepository).createPaymentOrder(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should handle payment response parsing error")
    void shouldHandlePaymentResponseParsingError() {
        // Arrange
        when(paymentRepository.createPaymentOrder(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("invalid json");

        List<OrderItem> orderItems = List.of(OrderItem.create(product, 1));
        Order testOrder = Order.create("12345678900", orderItems);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderUseCase.createPaymentOrder(testOrder, customerData);
        });
    }
}
