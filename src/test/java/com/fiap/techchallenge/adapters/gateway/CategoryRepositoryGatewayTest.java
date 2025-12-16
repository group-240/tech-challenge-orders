package com.fiap.techchallenge.adapters.gateway;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.external.datasource.entities.CategoryJpaEntity;
import com.fiap.techchallenge.external.datasource.repositories.CategoryJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryGatewayTest {

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @InjectMocks
    private CategoryRepositoryGateway categoryRepositoryGateway;

    private Category category;
    private CategoryJpaEntity categoryJpaEntity;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Electronics");
        categoryJpaEntity = new CategoryJpaEntity(categoryId, "Electronics");
    }

    @Test
    @DisplayName("Should save category successfully")
    void testShouldSaveCategorySuccessfully() {
        // Arrange
        when(categoryJpaRepository.save(any(CategoryJpaEntity.class))).thenReturn(categoryJpaEntity);

        // Act
        Category result = categoryRepositoryGateway.save(category);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryJpaRepository).save(any(CategoryJpaEntity.class));
    }

    @Test
    @DisplayName("Should find category by id successfully")
    void testShouldFindCategoryByIdSuccessfully() {
        // Arrange
        when(categoryJpaRepository.findById(categoryId)).thenReturn(Optional.of(categoryJpaEntity));

        // Act
        Optional<Category> result = categoryRepositoryGateway.findById(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(categoryId, result.get().getId());
        assertEquals("Electronics", result.get().getName());
        verify(categoryJpaRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Should return empty when category not found by id")
    void testShouldReturnEmptyWhenCategoryNotFoundById() {
        // Arrange
        when(categoryJpaRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryRepositoryGateway.findById(categoryId);

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryJpaRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Should check if category exists by name")
    void testShouldCheckIfCategoryExistsByName() {
        // Arrange
        String categoryName = "Electronics";
        when(categoryJpaRepository.existsByName(categoryName)).thenReturn(true);

        // Act
        boolean result = categoryRepositoryGateway.existsByName(categoryName);

        // Assert
        assertTrue(result);
        verify(categoryJpaRepository).existsByName(categoryName);
    }

    @Test
    @DisplayName("Should find all categories successfully")
    void testShouldFindAllCategoriesSuccessfully() {
        // Arrange
        List<CategoryJpaEntity> jpaEntities = List.of(categoryJpaEntity);
        when(categoryJpaRepository.findAll()).thenReturn(jpaEntities);

        // Act
        List<Category> result = categoryRepositoryGateway.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(categoryId, result.get(0).getId());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryJpaRepository).findAll();
    }

    @Test
    @DisplayName("Should delete category by id successfully")
    void testShouldDeleteCategoryByIdSuccessfully() {
        // Arrange
        doNothing().when(categoryJpaRepository).deleteById(categoryId);

        // Act
        categoryRepositoryGateway.deleteById(categoryId);

        // Assert
        verify(categoryJpaRepository).deleteById(categoryId);
    }
}