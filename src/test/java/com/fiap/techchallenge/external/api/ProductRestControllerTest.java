package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.adapters.controllers.ProductController;
import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTest {

    @Mock
    private ProductController productController;

    @InjectMocks
    private ProductRestController productRestController;

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
        ProductRestController.ProductRequestDTO requestDTO = new ProductRestController.ProductRequestDTO();
        requestDTO.setName("Laptop");
        requestDTO.setDescription("Gaming laptop");
        requestDTO.setPrice(BigDecimal.valueOf(2500.00));
        requestDTO.setCategoryId(categoryId);

        when(productController.createProduct("Laptop", "Gaming laptop", BigDecimal.valueOf(2500.00), categoryId))
                .thenReturn(product);

        // Act
        ResponseEntity<Product> response = productRestController.createProduct(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productController).createProduct("Laptop", "Gaming laptop", BigDecimal.valueOf(2500.00), categoryId);
    }

    @Test
    @DisplayName("Should find product by id successfully")
    void shouldFindProductByIdSuccessfully() {
        // Arrange
        when(productController.findProductById(productId)).thenReturn(Optional.of(product));

        // Act
        ResponseEntity<Product> response = productRestController.findProductById(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productController).findProductById(productId);
    }

    @Test
    @DisplayName("Should return not found when product does not exist")
    void shouldReturnNotFoundWhenProductDoesNotExist() {
        // Arrange
        when(productController.findProductById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = productRestController.findProductById(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productController).findProductById(productId);
    }

    @Test
    @DisplayName("Should find all products successfully")
    void shouldFindAllProductsSuccessfully() {
        // Arrange
        List<Product> products = List.of(product);
        when(productController.findAllProducts()).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = productRestController.findAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(productController).findAllProducts();
    }

    @Test
    @DisplayName("Should find products by name successfully")
    void shouldFindProductsByNameSuccessfully() {
        // Arrange
        String name = "Laptop";
        List<Product> products = List.of(product);
        when(productController.findProductsByName(name)).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = productRestController.findProductsByName(name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(productController).findProductsByName(name);
    }

    @Test
    @DisplayName("Should find products by category successfully")
    void shouldFindProductsByCategorySuccessfully() {
        // Arrange
        List<Product> products = List.of(product);
        when(productController.findProductsByCategory(categoryId)).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = productRestController.findProductsByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(productController).findProductsByCategory(categoryId);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        ProductRestController.ProductRequestDTO requestDTO = new ProductRestController.ProductRequestDTO();
        requestDTO.setName("Updated Laptop");
        requestDTO.setDescription("Updated description");
        requestDTO.setPrice(BigDecimal.valueOf(3000.00));
        requestDTO.setCategoryId(categoryId);

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Updated Laptop")
                .description("Updated description")
                .price(BigDecimal.valueOf(3000.00))
                .category(product.getCategory())
                .active(true)
                .build();

        when(productController.updateProduct(productId, "Updated Laptop", "Updated description", 
                BigDecimal.valueOf(3000.00), categoryId)).thenReturn(updatedProduct);

        // Act
        ResponseEntity<Product> response = productRestController.updateProduct(productId, requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
        verify(productController).updateProduct(productId, "Updated Laptop", "Updated description", 
                BigDecimal.valueOf(3000.00), categoryId);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        doNothing().when(productController).deleteProduct(productId);

        // Act
        ResponseEntity<String> response = productRestController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Produto deletado com sucesso.", response.getBody());
        verify(productController).deleteProduct(productId);
    }
}