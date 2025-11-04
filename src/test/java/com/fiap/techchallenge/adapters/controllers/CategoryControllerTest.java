package com.fiap.techchallenge.adapters.controllers;

import com.fiap.techchallenge.application.usecases.CategoryUseCase;
import com.fiap.techchallenge.domain.entities.Category;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryUseCase categoryUseCase;

    @InjectMocks
    private CategoryController categoryController;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Electronics");
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Arrange
        String categoryName = "Electronics";
        when(categoryUseCase.createCategory(categoryName)).thenReturn(category);

        // Act
        Category result = categoryController.createCategory(categoryName);

        // Assert
        assertEquals(category, result);
        verify(categoryUseCase).createCategory(categoryName);
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        // Arrange
        String newName = "Updated Electronics";
        Category updatedCategory = new Category(categoryId, newName);
        when(categoryUseCase.updateCategory(categoryId, newName)).thenReturn(updatedCategory);

        // Act
        Category result = categoryController.updateCategory(categoryId, newName);

        // Assert
        assertEquals(updatedCategory, result);
        verify(categoryUseCase).updateCategory(categoryId, newName);
    }

    @Test
    @DisplayName("Should find category by id successfully")
    void shouldFindCategoryByIdSuccessfully() {
        // Arrange
        when(categoryUseCase.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryController.findById(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(category, result.get());
        verify(categoryUseCase).findById(categoryId);
    }

    @Test
    @DisplayName("Should find all categories successfully")
    void shouldFindAllCategoriesSuccessfully() {
        // Arrange
        List<Category> categories = List.of(category);
        when(categoryUseCase.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryController.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(category, result.get(0));
        verify(categoryUseCase).findAll();
    }

    @Test
    @DisplayName("Should delete category by id successfully")
    void shouldDeleteCategoryByIdSuccessfully() {
        // Arrange
        doNothing().when(categoryUseCase).deleteById(categoryId);

        // Act
        categoryController.deleteById(categoryId);

        // Assert
        verify(categoryUseCase).deleteById(categoryId);
    }
}