package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.adapters.controllers.CategoryController;
import com.fiap.techchallenge.domain.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRestControllerTest {

    @Mock
    private CategoryController categoryController;

    @InjectMocks
    private CategoryRestController categoryRestController;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Electronics");
    }

    @Test
    @DisplayName("Should create category successfully")
    void testShouldCreateCategorySuccessfully() {
        // Arrange
        CategoryRestController.CategoryRequestDTO requestDTO = new CategoryRestController.CategoryRequestDTO();
        requestDTO.setName("Electronics");
        when(categoryController.createCategory("Electronics")).thenReturn(category);

        // Act
        ResponseEntity<Category> response = categoryRestController.createCategory(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoryController).createCategory("Electronics");
    }

    @Test
    @DisplayName("Should find category by id successfully")
    void testShouldFindCategoryByIdSuccessfully() {
        // Arrange
        when(categoryController.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        ResponseEntity<Category> response = categoryRestController.findById(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoryController).findById(categoryId);
    }

    @Test
    @DisplayName("Should return not found when category does not exist")
    void testShouldReturnNotFoundWhenCategoryDoesNotExist() {
        // Arrange
        when(categoryController.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Category> response = categoryRestController.findById(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryController).findById(categoryId);
    }

    @Test
    @DisplayName("Should find all categories successfully")
    void testShouldFindAllCategoriesSuccessfully() {
        // Arrange
        List<Category> categories = List.of(category);
        when(categoryController.findAll()).thenReturn(categories);

        // Act
        ResponseEntity<List<Category>> response = categoryRestController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
        verify(categoryController).findAll();
    }

    @Test
    @DisplayName("Should update category successfully")
    void testShouldUpdateCategorySuccessfully() {
        // Arrange
        CategoryRestController.CategoryRequestDTO requestDTO = new CategoryRestController.CategoryRequestDTO();
        requestDTO.setName("Updated Electronics");
        Category updatedCategory = new Category(categoryId, "Updated Electronics");
        when(categoryController.updateCategory(categoryId, "Updated Electronics")).thenReturn(updatedCategory);

        // Act
        ResponseEntity<Category> response = categoryRestController.updateCategory(categoryId, requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
        verify(categoryController).updateCategory(categoryId, "Updated Electronics");
    }

    @Test
    @DisplayName("Should delete category successfully")
    void testShouldDeleteCategorySuccessfully() {
        // Arrange
        doNothing().when(categoryController).deleteById(categoryId);

        // Act
        ResponseEntity<String> response = categoryRestController.deleteById(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Categoria deletada com sucesso.", response.getBody());
        verify(categoryController).deleteById(categoryId);
    }
}