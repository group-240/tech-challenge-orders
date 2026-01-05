package com.fiap.techchallenge.adapters.gateway;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import com.fiap.techchallenge.external.datasource.entities.CategoryJpaEntity;
import com.fiap.techchallenge.external.datasource.entities.ProductJpaEntity;
import com.fiap.techchallenge.external.datasource.repositories.ProductJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryGatewayTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private ProductRepositoryGateway productRepositoryGateway;

    private Product product;
    private ProductJpaEntity productJpaEntity;
    private UUID productId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        
        Category category = new Category(categoryId, "Electronics");
        CategoryJpaEntity categoryJpa = new CategoryJpaEntity(categoryId, "Electronics");
        
        product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(2500.00))
                .category(category)
                .active(true)
                .build();
        
        productJpaEntity = new ProductJpaEntity(productId, "Laptop", "Gaming laptop", 
                BigDecimal.valueOf(2500.00), categoryJpa, true);
    }

    @Test
    @DisplayName("Should save product successfully")
    void testShouldSaveProductSuccessfully() {
        // Arrange
        when(productJpaRepository.save(any(ProductJpaEntity.class))).thenReturn(productJpaEntity);

        // Act
        Product result = productRepositoryGateway.save(product);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals("Gaming laptop", result.getDescription());
        assertEquals(BigDecimal.valueOf(2500.00), result.getPrice());
        assertTrue(result.isActive());
        verify(productJpaRepository).save(any(ProductJpaEntity.class));
    }

    @Test
    @DisplayName("Should find product by id successfully")
    void testShouldFindProductByIdSuccessfully() {
        // Arrange
        when(productJpaRepository.findById(productId)).thenReturn(Optional.of(productJpaEntity));

        // Act
        Optional<Product> result = productRepositoryGateway.findById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
        assertEquals("Laptop", result.get().getName());
        verify(productJpaRepository).findById(productId);
    }

    @Test
    @DisplayName("Should return empty when product not found by id")
    void testShouldReturnEmptyWhenProductNotFoundById() {
        // Arrange
        when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productRepositoryGateway.findById(productId);

        // Assert
        assertTrue(result.isEmpty());
        verify(productJpaRepository).findById(productId);
    }

    @Test
    @DisplayName("Should find products by name successfully")
    void testShouldFindProductsByNameSuccessfully() {
        // Arrange
        String name = "Laptop";
        List<ProductJpaEntity> jpaEntities = List.of(productJpaEntity);
        when(productJpaRepository.findByNameContainingIgnoreCase(name)).thenReturn(jpaEntities);

        // Act
        List<Product> result = productRepositoryGateway.findByName(name);

        // Assert
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getId());
        assertEquals("Laptop", result.get(0).getName());
        verify(productJpaRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    @DisplayName("Should find products by category id successfully")
    void testShouldFindProductsByCategoryIdSuccessfully() {
        // Arrange
        List<ProductJpaEntity> jpaEntities = List.of(productJpaEntity);
        when(productJpaRepository.findByCategoryId(categoryId)).thenReturn(jpaEntities);

        // Act
        List<Product> result = productRepositoryGateway.findByCategoryId(categoryId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getId());
        assertEquals(categoryId, result.get(0).getCategory().getId());
        verify(productJpaRepository).findByCategoryId(categoryId);
    }

    @Test
    @DisplayName("Should find all products successfully")
    void testShouldFindAllProductsSuccessfully() {
        // Arrange
        List<ProductJpaEntity> jpaEntities = List.of(productJpaEntity);
        when(productJpaRepository.findAll()).thenReturn(jpaEntities);

        // Act
        List<Product> result = productRepositoryGateway.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getId());
        assertEquals("Laptop", result.get(0).getName());
        verify(productJpaRepository).findAll();
    }

    @Test
    @DisplayName("Should delete product by id successfully")
    void testShouldDeleteProductByIdSuccessfully() {
        // Arrange
        doNothing().when(productJpaRepository).deleteById(productId);

        // Act
        productRepositoryGateway.deleteById(productId);

        // Assert
        verify(productJpaRepository).deleteById(productId);
    }
}