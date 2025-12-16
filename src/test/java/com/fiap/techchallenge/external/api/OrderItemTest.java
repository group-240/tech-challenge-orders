package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.OrderItem;
import com.fiap.techchallenge.domain.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    @Test
    @DisplayName("Deve criar um OrderItem usando o construtor")
    public void testShouldCreateOrderItemUsingConstructor() {
        UUID productId = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Electronics");
        Product product = new Product(productId, "Laptop", "High-end laptop", new BigDecimal("3500.00"), category, true);

        UUID id = UUID.randomUUID();
        Integer quantity = 2;
        BigDecimal unitPrice = product.getPrice();
        BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        OrderItem item = new OrderItem(id, productId, product, quantity, unitPrice, subTotal);

        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(product, item.getProduct());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(subTotal, item.getSubTotal());
    }

    @Test
    @DisplayName("Deve criar um OrderItem usando os setters")
    public void testShouldCreateOrderItemUsingSetters() {
        UUID productId = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Books");
        Product product = new Product(productId, "Book", "A great book", new BigDecimal("120.00"), category, true);

        UUID id = UUID.randomUUID();
        Integer quantity = 3;
        BigDecimal unitPrice = product.getPrice();
        BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        OrderItem item = new OrderItem();
        item.setId(id);
        item.setProductId(productId);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setSubTotal(subTotal);

        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(product, item.getProduct());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(subTotal, item.getSubTotal());
    }

    @Test
    @DisplayName("Deve criar um OrderItem usando o método de fábrica")
    public void testShouldCreateOrderItemUsingFactoryMethod() {
        Category category = new Category(UUID.randomUUID(), "Clothing");
        Product product = new Product(UUID.randomUUID(), "T-Shirt", "Cotton T-Shirt", new BigDecimal("80.00"), category, true);

        Integer quantity = 5;
        BigDecimal expectedSubTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        OrderItem item = OrderItem.create(product, quantity);

        assertNull(item.getId()); // id deve ser null pelo factory
        assertEquals(product.getId(), item.getProductId());
        assertEquals(product, item.getProduct());
        assertEquals(quantity, item.getQuantity());
        assertEquals(product.getPrice(), item.getUnitPrice());
        assertEquals(expectedSubTotal, item.getSubTotal());
    }
}
