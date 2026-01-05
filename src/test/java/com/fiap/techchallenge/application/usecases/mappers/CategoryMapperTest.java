package com.fiap.techchallenge.application.usecases.mappers;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.external.datasource.entities.CategoryJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    @Test
    @DisplayName("Should map Category to CategoryJpaEntity")
    void testShouldMapCategoryToCategoryJpaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        Category category = new Category(id, "Electronics");

        // Act
        CategoryJpaEntity result = CategoryMapper.toJpaEntity(category);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Electronics", result.getName());
    }

    @Test
    @DisplayName("Should map CategoryJpaEntity to Category")
    void testShouldMapCategoryJpaEntityToCategory() {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryJpaEntity jpaEntity = new CategoryJpaEntity(id, "Books");

        // Act
        Category result = CategoryMapper.toDomainEntity(jpaEntity);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Books", result.getName());
    }

    @Test
    @DisplayName("Should handle null Category in toJpaEntity")
    void testShouldHandleNullCategoryInToJpaEntity() {
        // Act
        CategoryJpaEntity result = CategoryMapper.toJpaEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null CategoryJpaEntity in toDomainEntity")
    void testShouldHandleNullCategoryJpaEntityInToDomainEntity() {
        // Act
        Category result = CategoryMapper.toDomainEntity(null);

        // Assert
        assertNull(result);
    }
}