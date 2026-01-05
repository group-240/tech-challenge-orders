package com.fiap.techchallenge.adapters.gateway;

import com.fiap.techchallenge.domain.entities.*;
import com.fiap.techchallenge.external.datasource.entities.OrderJpaEntity;
import com.fiap.techchallenge.external.datasource.repositories.OrderJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryGatewayTest {

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @InjectMocks
    private OrderRepositoryGateway orderRepositoryGateway;

    private Order order;
    private OrderJpaEntity orderJpaEntity;

    @BeforeEach
    void setUp() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        
        Category category = new Category(categoryId, "Test Category");
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

        orderJpaEntity = new OrderJpaEntity();
        orderJpaEntity.setId(1L);
        orderJpaEntity.setCpf("12345678900");
        orderJpaEntity.setTotalAmount(BigDecimal.valueOf(20.00));
        orderJpaEntity.setStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED);
        orderJpaEntity.setStatusPayment(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO);
        orderJpaEntity.setIdPayment(123L);
        orderJpaEntity.setCreatedAt(LocalDateTime.now());
        orderJpaEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save order successfully")
    void testShouldSaveOrderSuccessfully() {
        // Arrange
        when(orderJpaRepository.save(any(OrderJpaEntity.class))).thenReturn(orderJpaEntity);

        // Act
        Order result = orderRepositoryGateway.save(order);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCpf());
        verify(orderJpaRepository).save(any(OrderJpaEntity.class));
    }

    @Test
    @DisplayName("Should find order by id successfully")
    void testShouldFindOrderByIdSuccessfully() {
        // Arrange
        when(orderJpaRepository.findById(1L)).thenReturn(Optional.of(orderJpaEntity));

        // Act
        Optional<Order> result = orderRepositoryGateway.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(orderJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when order not found by id")
    void testShouldReturnEmptyWhenOrderNotFoundById() {
        // Arrange
        when(orderJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderRepositoryGateway.findById(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(orderJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find order by payment id successfully")
    void testShouldFindOrderByPaymentIdSuccessfully() {
        // Arrange
        when(orderJpaRepository.findByIdPayment(123L)).thenReturn(Optional.of(orderJpaEntity));

        // Act
        Optional<Order> result = orderRepositoryGateway.findByIdPayment(123L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(123L, result.get().getIdPayment());
        verify(orderJpaRepository).findByIdPayment(123L);
    }

    @Test
    @DisplayName("Should find orders by status successfully")
    void testShouldFindOrdersByStatusSuccessfully() {
        // Arrange
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED))
                .thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findByOptionalStatus(OrderStatus.RECEIVED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(OrderStatus.RECEIVED, result.get(0).getStatus());
        verify(orderJpaRepository).findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED);
    }

    @Test
    @DisplayName("Should find all orders when status is null")
    void testShouldFindAllOrdersWhenStatusIsNull() {
        // Arrange
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findByOptionalStatus(null)).thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findByOptionalStatus(null);

        // Assert
        assertEquals(1, result.size());
        verify(orderJpaRepository).findByOptionalStatus(null);
    }

    @Test
    @DisplayName("Should find all orders successfully")
    void testShouldFindAllOrdersSuccessfully() {
        // Arrange
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findAll()).thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findAll();

        // Assert
        assertEquals(1, result.size());
        verify(orderJpaRepository).findAll();
    }

    @Test
    @DisplayName("Should check if order exists by product id")
    void testShouldCheckIfOrderExistsByProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();
        when(orderJpaRepository.existsByItemsProductId(productId)).thenReturn(true);

        // Act
        boolean result = orderRepositoryGateway.existsByProductId(productId);

        // Assert
        assertTrue(result);
        verify(orderJpaRepository).existsByItemsProductId(productId);
    }

    @Test
    @DisplayName("Should return false when order does not exist by product id")
    void testShouldReturnFalseWhenOrderDoesNotExistByProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();
        when(orderJpaRepository.existsByItemsProductId(productId)).thenReturn(false);

        // Act
        boolean result = orderRepositoryGateway.existsByProductId(productId);

        // Assert
        assertFalse(result);
        verify(orderJpaRepository).existsByItemsProductId(productId);
    }

    @Test
    @DisplayName("Should find orders by IN_PREPARATION status")
    void testShouldFindOrdersByInPreparationStatus() {
        // Arrange
        orderJpaEntity.setStatus(OrderJpaEntity.OrderStatusJpa.IN_PREPARATION);
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.IN_PREPARATION))
                .thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findByOptionalStatus(OrderStatus.IN_PREPARATION);

        // Assert
        assertEquals(1, result.size());
        verify(orderJpaRepository).findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.IN_PREPARATION);
    }

    @Test
    @DisplayName("Should find orders by READY status")
    void testShouldFindOrdersByReadyStatus() {
        // Arrange
        orderJpaEntity.setStatus(OrderJpaEntity.OrderStatusJpa.READY);
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.READY))
                .thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findByOptionalStatus(OrderStatus.READY);

        // Assert
        assertEquals(1, result.size());
        verify(orderJpaRepository).findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.READY);
    }

    @Test
    @DisplayName("Should find orders by FINISHED status")
    void testShouldFindOrdersByFinishedStatus() {
        // Arrange
        orderJpaEntity.setStatus(OrderJpaEntity.OrderStatusJpa.FINISHED);
        List<OrderJpaEntity> jpaEntities = List.of(orderJpaEntity);
        when(orderJpaRepository.findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.FINISHED))
                .thenReturn(jpaEntities);

        // Act
        List<Order> result = orderRepositoryGateway.findByOptionalStatus(OrderStatus.FINISHED);

        // Assert
        assertEquals(1, result.size());
        verify(orderJpaRepository).findByOptionalStatus(OrderJpaEntity.OrderStatusJpa.FINISHED);
    }
}
