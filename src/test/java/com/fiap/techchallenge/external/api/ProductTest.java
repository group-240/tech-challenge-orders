package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.domain.entities.Category;
import com.fiap.techchallenge.domain.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    @DisplayName("Deve criar um produto usando o construtor")
    public void testShouldCreateProductUsingConstructor() {
        UUID id = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Electronics");
        String name = "Laptop";
        String description = "High-end laptop";
        BigDecimal price = new BigDecimal("3500.00");
        boolean active = true;

        Product product = new Product(id, name, description, price, category, active);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertTrue(product.isActive());
    }

    @Test
    @DisplayName("Deve criar um produto usando os setters")
    public void testShouldCreateProductUsingSetters() {
        UUID id = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Books");
        String name = "Book";
        String description = "A good book";
        BigDecimal price = new BigDecimal("120.00");
        boolean active = false;

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setActive(active);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertFalse(product.isActive());
    }

    @Test
    @DisplayName("Deve criar um produto usando o Builder")
    public void testShouldCreateProductUsingBuilder() {
        UUID id = UUID.randomUUID();
        Category category = new Category(UUID.randomUUID(), "Clothing");
        String name = "T-Shirt";
        String description = "Cotton T-Shirt";
        BigDecimal price = new BigDecimal("80.00");
        boolean active = true;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .active(active)
                .build();

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertTrue(product.isActive());
    }

    @Test
    @DisplayName("Deve ativar um produto")
    public void testShouldActivateProduct() {
        Product product = Product.builder().active(false).build();
        Product activated = product.activate();
        assertTrue(activated.isActive());
    }

    @Test
    @DisplayName("Deve desativar um produto")
    public void testShouldDeactivateProduct() {
        Product product = Product.builder().active(true).build();
        Product deactivated = product.deactivate();
        assertFalse(deactivated.isActive());
    }

    @Test
    @DisplayName("Deve atualizar um produto")
    public void testShouldUpdateProduct() {
        Category oldCategory = new Category(UUID.randomUUID(), "Old");
        Category newCategory = new Category(UUID.randomUUID(), "New");

        Product product = Product.builder()
                .name("Old Name")
                .description("Old Description")
                .price(new BigDecimal("100"))
                .category(oldCategory)
                .active(true)
                .build();

        Product updated = product.update("New Name", "New Description", new BigDecimal("200"), newCategory);

        assertEquals("New Name", updated.getName());
        assertEquals("New Description", updated.getDescription());
        assertEquals(new BigDecimal("200"), updated.getPrice());
        assertEquals(newCategory, updated.getCategory());
        assertTrue(updated.isActive()); // ativa permanece igual
    }

    @Test
    @DisplayName("Deve atualizar apenas os campos fornecidos")
    public void testShouldUpdateOnlyProvidedFields() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Old Name", "Old Desc", BigDecimal.valueOf(100), new Category(UUID.randomUUID(), "Books"), true);

        Category newCategory = new Category(UUID.randomUUID(), "Electronics");
        Product updatedProduct = product.update("New Name", null, BigDecimal.valueOf(200), newCategory);

        assertEquals(id, updatedProduct.getId());
        assertEquals("New Name", updatedProduct.getName());        // atualizado
        assertEquals("Old Desc", updatedProduct.getDescription()); // manteve antigo
        assertEquals(BigDecimal.valueOf(200), updatedProduct.getPrice()); // atualizado
        assertEquals(newCategory, updatedProduct.getCategory());   // atualizado
        assertTrue(updatedProduct.isActive());                     // manteve ativo
    }
}
