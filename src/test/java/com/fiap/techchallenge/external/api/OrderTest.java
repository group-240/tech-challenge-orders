package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.domain.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Product createProduct(String name, BigDecimal price) {
        UUID productId = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Test Category");
        return new Product(productId, name, "Description of " + name, price, category, true);
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        UUID orderItemId = UUID.randomUUID();
        return new OrderItem(orderItemId, null, product, quantity, product.getPrice(), product.getPrice().multiply(BigDecimal.valueOf(quantity)));
    }

    @Test
    @DisplayName("Deve criar um pedido usando o construtor")
    public void shouldCreateOrderUsingConstructor() {
        Product product = createProduct("Product A", new BigDecimal("50.00"));
        OrderItem item = createOrderItem(product, 2);

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order(
                1L,
                "12345678900",
                List.of(item),
                item.getSubTotal(),
                OrderStatus.RECEIVED,
                StatusPayment.AGUARDANDO_PAGAMENTO,
                100L,
                now,
                now
        );

        assertEquals(1L, order.getId());
        assertEquals("12345678900", order.getCpf());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("100.00"), order.getTotalAmount());
        assertEquals(OrderStatus.RECEIVED, order.getStatus());
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, order.getStatusPayment());
        assertEquals(100L, order.getIdPayment());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar um pedido usando os setters")
    public void shouldCreateOrderUsingSetters() {
        Product product = createProduct("Product B", new BigDecimal("75.00"));
        OrderItem item = createOrderItem(product, 1);

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setId(2L);
        order.setCpf("09876543211");
        order.setItems(List.of(item));
        order.setTotalAmount(item.getSubTotal());
        order.setStatus(OrderStatus.RECEIVED);
        order.setStatusPayment(StatusPayment.AGUARDANDO_PAGAMENTO);
        order.setIdPayment(101L);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        assertEquals(2L, order.getId());
        assertEquals("09876543211", order.getCpf());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("75.00"), order.getTotalAmount());
        assertEquals(OrderStatus.RECEIVED, order.getStatus());
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, order.getStatusPayment());
        assertEquals(101L, order.getIdPayment());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar um pedido usando o método de fábrica")
    public void shouldCreateOrderUsingFactory() {
        Product product = createProduct("Product C", new BigDecimal("20.00"));
        OrderItem item = createOrderItem(product, 3);

        Order order = Order.create("11122233344", List.of(item));

        assertEquals("11122233344", order.getCpf());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("60.00"), order.getTotalAmount());
        assertEquals(OrderStatus.RECEIVED, order.getStatus());
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, order.getStatusPayment());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }
}
