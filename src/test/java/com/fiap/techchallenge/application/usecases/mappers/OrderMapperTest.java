package com.fiap.techchallenge.application.usecases.mappers;

import com.fiap.techchallenge.domain.entities.*;
import com.fiap.techchallenge.external.datasource.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private Order order;
    private OrderJpaEntity orderJpaEntity;
    private Product product;
    private ProductJpaEntity productJpaEntity;
    private OrderItem orderItem;
    private OrderItemJpaEntity orderItemJpaEntity;

    @BeforeEach
    void setUp() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID orderItemId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Setup Category
        Category category = new Category(categoryId, "Electronics");
        CategoryJpaEntity categoryJpa = new CategoryJpaEntity(categoryId, "Electronics");

        // Setup Product
        product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(2500.00))
                .category(category)
                .active(true)
                .build();

        productJpaEntity = new ProductJpaEntity(productId, "Laptop", "Gaming laptop", 
                BigDecimal.valueOf(2500.00), categoryJpa, true);

        // Setup OrderItem
        orderItem = new OrderItem(orderItemId, productId, product, 2, 
                BigDecimal.valueOf(2500.00), BigDecimal.valueOf(5000.00));

        orderItemJpaEntity = new OrderItemJpaEntity(orderItemId, productId, productJpaEntity, 
                2, BigDecimal.valueOf(2500.00), BigDecimal.valueOf(5000.00));

        // Setup Order
        order = new Order(1L, "12345678900", List.of(orderItem), BigDecimal.valueOf(5000.00), 
                OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, now, now);

        // Setup OrderJpaEntity
        orderJpaEntity = new OrderJpaEntity();
        orderJpaEntity.setId(1L);
        orderJpaEntity.setCpf("12345678900");
        orderJpaEntity.setItems(List.of(orderItemJpaEntity));
        orderJpaEntity.setTotalAmount(BigDecimal.valueOf(5000.00));
        orderJpaEntity.setStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED);
        orderJpaEntity.setStatusPayment(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO);
        orderJpaEntity.setIdPayment(123L);
        orderJpaEntity.setCreatedAt(now);
        orderJpaEntity.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should map Order to OrderJpaEntity")
    void shouldMapOrderToOrderJpaEntity() {
        // Act
        OrderJpaEntity result = OrderMapper.toJpaEntity(order);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCpf());
        assertEquals(BigDecimal.valueOf(5000.00), result.getTotalAmount());
        assertEquals(OrderJpaEntity.OrderStatusJpa.RECEIVED, result.getStatus());
        assertEquals(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO, result.getStatusPayment());
        assertEquals(123L, result.getIdPayment());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        
        // Verify items
        assertEquals(1, result.getItems().size());
        OrderItemJpaEntity itemJpa = result.getItems().get(0);
        assertEquals(orderItem.getId(), itemJpa.getId());
        assertEquals(orderItem.getProductId(), itemJpa.getProductId());
        assertEquals(2, itemJpa.getQuantity());
        assertEquals(BigDecimal.valueOf(2500.00), itemJpa.getUnitPrice());
        assertEquals(BigDecimal.valueOf(5000.00), itemJpa.getSubTotal());
    }

    @Test
    @DisplayName("Should map OrderJpaEntity to Order")
    void shouldMapOrderJpaEntityToOrder() {
        // Act
        Order result = OrderMapper.toDomainEntity(orderJpaEntity);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCpf());
        assertEquals(BigDecimal.valueOf(5000.00), result.getTotalAmount());
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, result.getStatusPayment());
        assertEquals(123L, result.getIdPayment());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        
        // Verify items
        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().get(0);
        assertEquals(orderItem.getId(), item.getId());
        assertEquals(orderItem.getProductId(), item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(BigDecimal.valueOf(2500.00), item.getUnitPrice());
        assertEquals(BigDecimal.valueOf(5000.00), item.getSubTotal());
    }

    @Test
    @DisplayName("Should handle null Order in toJpaEntity")
    void shouldHandleNullOrderInToJpaEntity() {
        // Act
        OrderJpaEntity result = OrderMapper.toJpaEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null OrderJpaEntity in toDomainEntity")
    void shouldHandleNullOrderJpaEntityInToDomainEntity() {
        // Act
        Order result = OrderMapper.toDomainEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle Order with null items")
    void shouldHandleOrderWithNullItems() {
        // Arrange
        Order orderWithNullItems = new Order(1L, "12345678900", null, BigDecimal.valueOf(1000.00), 
                OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());

        // Act
        OrderJpaEntity result = OrderMapper.toJpaEntity(orderWithNullItems);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCpf());
        assertNull(result.getItems());
    }

    @Test
    @DisplayName("Should handle OrderJpaEntity with null items")
    void shouldHandleOrderJpaEntityWithNullItems() {
        // Arrange
        OrderJpaEntity jpaWithNullItems = new OrderJpaEntity();
        jpaWithNullItems.setId(1L);
        jpaWithNullItems.setCpf("12345678900");
        jpaWithNullItems.setItems(null);
        jpaWithNullItems.setTotalAmount(BigDecimal.valueOf(1000.00));
        jpaWithNullItems.setStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED);
        jpaWithNullItems.setStatusPayment(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO);
        jpaWithNullItems.setIdPayment(123L);
        jpaWithNullItems.setCreatedAt(LocalDateTime.now());
        jpaWithNullItems.setUpdatedAt(LocalDateTime.now());

        // Act
        Order result = OrderMapper.toDomainEntity(jpaWithNullItems);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("12345678900", result.getCpf());
        assertNull(result.getItems());
    }

    @Test
    @DisplayName("Should map all order statuses correctly")
    void shouldMapAllOrderStatusesCorrectly() {
        // Test RECEIVED
        Order receivedOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity receivedJpa = OrderMapper.toJpaEntity(receivedOrder);
        assertEquals(OrderJpaEntity.OrderStatusJpa.RECEIVED, receivedJpa.getStatus());
        
        Order receivedBack = OrderMapper.toDomainEntity(receivedJpa);
        assertEquals(OrderStatus.RECEIVED, receivedBack.getStatus());

        // Test IN_PREPARATION
        Order inPrepOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.IN_PREPARATION, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity inPrepJpa = OrderMapper.toJpaEntity(inPrepOrder);
        assertEquals(OrderJpaEntity.OrderStatusJpa.IN_PREPARATION, inPrepJpa.getStatus());
        
        Order inPrepBack = OrderMapper.toDomainEntity(inPrepJpa);
        assertEquals(OrderStatus.IN_PREPARATION, inPrepBack.getStatus());

        // Test READY
        Order readyOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.READY, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity readyJpa = OrderMapper.toJpaEntity(readyOrder);
        assertEquals(OrderJpaEntity.OrderStatusJpa.READY, readyJpa.getStatus());
        
        Order readyBack = OrderMapper.toDomainEntity(readyJpa);
        assertEquals(OrderStatus.READY, readyBack.getStatus());

        // Test FINISHED
        Order finishedOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.FINISHED, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity finishedJpa = OrderMapper.toJpaEntity(finishedOrder);
        assertEquals(OrderJpaEntity.OrderStatusJpa.FINISHED, finishedJpa.getStatus());
        
        Order finishedBack = OrderMapper.toDomainEntity(finishedJpa);
        assertEquals(OrderStatus.FINISHED, finishedBack.getStatus());
    }

    @Test
    @DisplayName("Should map all payment statuses correctly")
    void shouldMapAllPaymentStatusesCorrectly() {
        // Test AGUARDANDO_PAGAMENTO
        Order aguardandoOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity aguardandoJpa = OrderMapper.toJpaEntity(aguardandoOrder);
        assertEquals(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO, aguardandoJpa.getStatusPayment());
        
        Order aguardandoBack = OrderMapper.toDomainEntity(aguardandoJpa);
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, aguardandoBack.getStatusPayment());

        // Test APROVADO
        Order aprovadoOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.RECEIVED, StatusPayment.APROVADO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity aprovadoJpa = OrderMapper.toJpaEntity(aprovadoOrder);
        assertEquals(OrderJpaEntity.StatusPaymentJpa.APROVADO, aprovadoJpa.getStatusPayment());
        
        Order aprovadoBack = OrderMapper.toDomainEntity(aprovadoJpa);
        assertEquals(StatusPayment.APROVADO, aprovadoBack.getStatusPayment());

        // Test REJEITADO
        Order rejeitadoOrder = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.RECEIVED, StatusPayment.REJEITADO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity rejeitadoJpa = OrderMapper.toJpaEntity(rejeitadoOrder);
        assertEquals(OrderJpaEntity.StatusPaymentJpa.REJEITADO, rejeitadoJpa.getStatusPayment());
        
        Order rejeitadoBack = OrderMapper.toDomainEntity(rejeitadoJpa);
        assertEquals(StatusPayment.REJEITADO, rejeitadoBack.getStatusPayment());
    }

    @Test
    @DisplayName("Should handle null statuses in mapping")
    void shouldHandleNullStatusesInMapping() {
        // Test with null order status
        Order orderWithNullStatus = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                null, StatusPayment.AGUARDANDO_PAGAMENTO, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity result1 = OrderMapper.toJpaEntity(orderWithNullStatus);
        assertNull(result1.getStatus());

        // Test with null payment status
        Order orderWithNullPaymentStatus = new Order(1L, "12345678900", List.of(), BigDecimal.valueOf(100.00), 
                OrderStatus.RECEIVED, null, 123L, 
                LocalDateTime.now(), LocalDateTime.now());
        
        OrderJpaEntity result2 = OrderMapper.toJpaEntity(orderWithNullPaymentStatus);
        assertNull(result2.getStatusPayment());
    }

    @Test
    @DisplayName("Should handle OrderItem with null product")
    void shouldHandleOrderItemWithNullProduct() {
        // Arrange
        UUID orderItemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem itemWithNullProduct = new OrderItem(orderItemId, productId, null, 1, 
                BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
        
        Order orderWithNullProductItem = new Order(1L, "12345678900", List.of(itemWithNullProduct), 
                BigDecimal.valueOf(100.00), OrderStatus.RECEIVED, StatusPayment.AGUARDANDO_PAGAMENTO, 
                123L, LocalDateTime.now(), LocalDateTime.now());

        // Act
        OrderJpaEntity result = OrderMapper.toJpaEntity(orderWithNullProductItem);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        OrderItemJpaEntity itemJpa = result.getItems().get(0);
        assertEquals(orderItemId, itemJpa.getId());
        assertEquals(productId, itemJpa.getProductId());
        assertNull(itemJpa.getProduct()); // Product should be null
        assertEquals(1, itemJpa.getQuantity());
        assertEquals(BigDecimal.valueOf(100.00), itemJpa.getUnitPrice());
        assertEquals(BigDecimal.valueOf(100.00), itemJpa.getSubTotal());
    }

    @Test
    @DisplayName("Should handle OrderItemJpaEntity with null product")
    void shouldHandleOrderItemJpaEntityWithNullProduct() {
        // Arrange
        UUID orderItemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItemJpaEntity itemJpaWithNullProduct = new OrderItemJpaEntity(orderItemId, productId, 
                null, 1, BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
        
        OrderJpaEntity jpaWithNullProductItem = new OrderJpaEntity();
        jpaWithNullProductItem.setId(1L);
        jpaWithNullProductItem.setCpf("12345678900");
        jpaWithNullProductItem.setItems(List.of(itemJpaWithNullProduct));
        jpaWithNullProductItem.setTotalAmount(BigDecimal.valueOf(100.00));
        jpaWithNullProductItem.setStatus(OrderJpaEntity.OrderStatusJpa.RECEIVED);
        jpaWithNullProductItem.setStatusPayment(OrderJpaEntity.StatusPaymentJpa.AGUARDANDO_PAGAMENTO);
        jpaWithNullProductItem.setIdPayment(123L);
        jpaWithNullProductItem.setCreatedAt(LocalDateTime.now());
        jpaWithNullProductItem.setUpdatedAt(LocalDateTime.now());

        // Act
        Order result = OrderMapper.toDomainEntity(jpaWithNullProductItem);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().get(0);
        assertEquals(orderItemId, item.getId());
        assertEquals(productId, item.getProductId());
        assertNull(item.getProduct()); // Product should be null
        assertEquals(1, item.getQuantity());
        assertEquals(BigDecimal.valueOf(100.00), item.getUnitPrice());
        assertEquals(BigDecimal.valueOf(100.00), item.getSubTotal());
    }
}