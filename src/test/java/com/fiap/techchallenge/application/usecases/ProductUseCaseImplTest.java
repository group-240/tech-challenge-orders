package com.fiap.techchallenge.application.usecases;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.exception.ProductLinkedToOrderException;
import com.fiap.techchallenge.domain.repositories.CategoryRepository;
import com.fiap.techchallenge.domain.repositories.OrderRepository;
import com.fiap.techchallenge.domain.repositories.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    private UUID productId;
    private UUID categoryId;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new Category(categoryId, "Category Name");
        product = Product.builder()
                .id(productId)
                .name("Product Name")
                .description("Product Description")
                .price(BigDecimal.valueOf(100.0))
                .category(category)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create a product successfully")
    void testShouldCreateProductSuccessfully() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product createdProduct = productUseCase.createProduct("Product Name", "Product Description", BigDecimal.valueOf(100.0), categoryId);

        // Assert
        assertNotNull(createdProduct);
        assertEquals("Product Name", createdProduct.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when category is not found during product creation")
    void testShouldThrowNotFoundExceptionWhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productUseCase.createProduct("Product Name", "Product Description", BigDecimal.valueOf(100.0), categoryId);
        });
        assertEquals("Category Record not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void testShouldFindProductByIdSuccessfully() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> foundProduct = productUseCase.findProductById(productId);

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(productId, foundProduct.get().getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when product is not found by ID")
    void testShouldThrowNotFoundExceptionWhenProductNotFoundById() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productUseCase.findProductById(productId);
        });
        assertEquals("Record not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should update product successfully")
    void testShouldUpdateProductSuccessfully() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // retorna o produto atualizado

        // Act
        Product updatedProduct = productUseCase.updateProduct(
                productId,
                "Updated Name",
                "Updated Description",
                BigDecimal.valueOf(200.0),
                categoryId
        );

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(200.0), updatedProduct.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product with null category successfully")
    void testShouldUpdateProductWithNullCategorySuccessfully() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product updatedProduct = productUseCase.updateProduct(
                productId,
                "Updated Name",
                "Updated Description",
                BigDecimal.valueOf(200.0),
                null
        );

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(200.0), updatedProduct.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, never()).findById(any());
        verify(productRepository, times(1)).save(any(Product.class));
    }


    @Test
    @DisplayName("Should throw NotFoundException when updating a product that does not exist")
    void testShouldThrowNotFoundExceptionWhenUpdatingNonExistentProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productUseCase.updateProduct(productId, "Updated Name", "Updated Description", BigDecimal.valueOf(200.0), categoryId);
        });
        assertEquals("Record not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testShouldDeleteProductSuccessfully() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.existsByProductId(productId)).thenReturn(false);

        // Act
        productUseCase.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(orderRepository, times(1)).existsByProductId(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting a product that does not exist")
    void testShouldThrowNotFoundExceptionWhenDeletingNonExistentProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productUseCase.deleteProduct(productId);
        });
        assertEquals("Record not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ProductLinkedToOrderException when deleting a product linked to an order")
    void testShouldThrowProductLinkedToOrderExceptionWhenDeletingLinkedProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.existsByProductId(productId)).thenReturn(true);

        // Act & Assert
        ProductLinkedToOrderException exception = assertThrows(ProductLinkedToOrderException.class, () -> {
            productUseCase.deleteProduct(productId);
        });
        assertEquals("Product is already linked to an order and cannot be deleted", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(orderRepository, times(1)).existsByProductId(productId);
    }
}