package com.fiap.techchallenge.application.usecases.mappers;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import com.fiap.techchallenge.external.datasource.entities.CategoryJpaEntity;
import com.fiap.techchallenge.external.datasource.entities.ProductJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    @DisplayName("Should map Product to ProductJpaEntity")
    void shouldMapProductToProductJpaEntity() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Electronics");
        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(2500.00))
                .category(category)
                .active(true)
                .build();

        // Act
        ProductJpaEntity result = ProductMapper.toJpaEntity(product);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals("Gaming laptop", result.getDescription());
        assertEquals(BigDecimal.valueOf(2500.00), result.getPrice());
        assertTrue(result.isActive());
        assertNotNull(result.getCategory());
        assertEquals(categoryId, result.getCategory().getId());
        assertEquals("Electronics", result.getCategory().getName());
    }

    @Test
    @DisplayName("Should map ProductJpaEntity to Product")
    void shouldMapProductJpaEntityToProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CategoryJpaEntity categoryJpa = new CategoryJpaEntity(categoryId, "Books");
        ProductJpaEntity jpaEntity = new ProductJpaEntity(productId, "Book", "Great book", 
                BigDecimal.valueOf(120.00), categoryJpa, false);

        // Act
        Product result = ProductMapper.toDomainEntity(jpaEntity);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Book", result.getName());
        assertEquals("Great book", result.getDescription());
        assertEquals(BigDecimal.valueOf(120.00), result.getPrice());
        assertFalse(result.isActive());
        assertNotNull(result.getCategory());
        assertEquals(categoryId, result.getCategory().getId());
        assertEquals("Books", result.getCategory().getName());
    }

    @Test
    @DisplayName("Should handle null Product in toJpaEntity")
    void shouldHandleNullProductInToJpaEntity() {
        // Act
        ProductJpaEntity result = ProductMapper.toJpaEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null ProductJpaEntity in toDomainEntity")
    void shouldHandleNullProductJpaEntityInToDomainEntity() {
        // Act
        Product result = ProductMapper.toDomainEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle Product with null category")
    void shouldHandleProductWithNullCategory() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Uncategorized Product")
                .description("No category")
                .price(BigDecimal.valueOf(100.00))
                .category(null)
                .active(true)
                .build();

        // Act
        ProductJpaEntity result = ProductMapper.toJpaEntity(product);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Uncategorized Product", result.getName());
        assertNull(result.getCategory());
    }

    @Test
    @DisplayName("Should handle ProductJpaEntity with null category")
    void shouldHandleProductJpaEntityWithNullCategory() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductJpaEntity jpaEntity = new ProductJpaEntity(productId, "Product", "Description", 
                BigDecimal.valueOf(50.00), null, true);

        // Act
        Product result = ProductMapper.toDomainEntity(jpaEntity);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Product", result.getName());
        assertNull(result.getCategory());
    }
}