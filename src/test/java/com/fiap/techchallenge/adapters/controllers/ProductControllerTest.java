package com.fiap.techchallenge.adapters.controllers;

import com.fiap.techchallenge.application.usecases.ProductUseCase;
import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private UUID productId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Electronics");
        product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(2500.00))
                .category(category)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Arrange
        String name = "Laptop";
        String description = "Gaming laptop";
        BigDecimal price = BigDecimal.valueOf(2500.00);
        
        when(productUseCase.createProduct(name, description, price, categoryId)).thenReturn(product);

        // Act
        Product result = productController.createProduct(name, description, price, categoryId);

        // Assert
        assertEquals(product, result);
        verify(productUseCase).createProduct(name, description, price, categoryId);
    }

    @Test
    @DisplayName("Should find product by id successfully")
    void shouldFindProductByIdSuccessfully() {
        // Arrange
        when(productUseCase.findProductById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productController.findProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productUseCase).findProductById(productId);
    }

    @Test
    @DisplayName("Should find products by name successfully")
    void shouldFindProductsByNameSuccessfully() {
        // Arrange
        String name = "Laptop";
        List<Product> products = List.of(product);
        when(productUseCase.findProductsByName(name)).thenReturn(products);

        // Act
        List<Product> result = productController.findProductsByName(name);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productUseCase).findProductsByName(name);
    }

    @Test
    @DisplayName("Should find all products successfully")
    void shouldFindAllProductsSuccessfully() {
        // Arrange
        List<Product> products = List.of(product);
        when(productUseCase.findAllProducts()).thenReturn(products);

        // Act
        List<Product> result = productController.findAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productUseCase).findAllProducts();
    }

    @Test
    @DisplayName("Should find products by category successfully")
    void shouldFindProductsByCategorySuccessfully() {
        // Arrange
        List<Product> products = List.of(product);
        when(productUseCase.findProductsByCategory(categoryId)).thenReturn(products);

        // Act
        List<Product> result = productController.findProductsByCategory(categoryId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productUseCase).findProductsByCategory(categoryId);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        String newName = "Updated Laptop";
        String newDescription = "Updated description";
        BigDecimal newPrice = BigDecimal.valueOf(3000.00);
        
        Product updatedProduct = Product.builder()
                .id(productId)
                .name(newName)
                .description(newDescription)
                .price(newPrice)
                .category(product.getCategory())
                .active(true)
                .build();
        
        when(productUseCase.updateProduct(productId, newName, newDescription, newPrice, categoryId))
                .thenReturn(updatedProduct);

        // Act
        Product result = productController.updateProduct(productId, newName, newDescription, newPrice, categoryId);

        // Assert
        assertEquals(updatedProduct, result);
        verify(productUseCase).updateProduct(productId, newName, newDescription, newPrice, categoryId);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        doNothing().when(productUseCase).deleteProduct(productId);

        // Act
        productController.deleteProduct(productId);

        // Assert
        verify(productUseCase).deleteProduct(productId);
    }
}