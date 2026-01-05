package com.fiap.techchallenge.application.usecases;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.repositories.CategoryRepository;
import com.fiap.techchallenge.domain.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryUseCaseImplTest {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private CategoryUseCaseImpl categoryUseCase;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        productRepository = mock(ProductRepository.class);
        categoryUseCase = new CategoryUseCaseImpl(categoryRepository, productRepository);
    }

    @Test
    void testCreateCategory_ValidInput() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(mock(Category.class));

        Category category = categoryUseCase.createCategory("Electronics");
        assertNotNull(category);
    }

    @Test
    void testCreateCategory_DuplicateName() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class,
                () -> categoryUseCase.createCategory("Electronics"));
        assertEquals("Category with name Electronics already exists", exception.getMessage());
    }

    @Test
    void testUpdateCategory_NotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryUseCase.updateCategory(id, "New Name"));
        assertEquals("Record not found", exception.getMessage());
    }

    @Test
    void testDeleteById_CategoryLinkedToProducts() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(mock(Category.class)));
        when(productRepository.findByCategoryId(id)).thenReturn(List.of(mock(Product.class)));

        DomainException exception = assertThrows(DomainException.class,
                () -> categoryUseCase.deleteById(id));
        assertEquals("Não é possível deletar a categoria pois ela está vinculada a um ou mais produtos",
                exception.getMessage());
    }

    @Test
    void testDeleteById_SuccessWhenNotLinkedToProducts() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(mock(Category.class)));
        when(productRepository.findByCategoryId(id)).thenReturn(List.of()); // Lista vazia - sem produtos

        categoryUseCase.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteById_CategoryNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryUseCase.deleteById(id));
        assertEquals("Record not found", exception.getMessage());
    }

    @Test
    void testUpdateCategory_WithBlankName() {
        UUID id = UUID.randomUUID();

        DomainException exception = assertThrows(DomainException.class,
                () -> categoryUseCase.updateCategory(id, ""));
        assertEquals("Category name cannot be blank.", exception.getMessage());
    }

    @Test
    void testUpdateCategory_WithNullName() {
        UUID id = UUID.randomUUID();

        DomainException exception = assertThrows(DomainException.class,
                () -> categoryUseCase.updateCategory(id, null));
        assertEquals("Category name cannot be blank.", exception.getMessage());
    }
}
