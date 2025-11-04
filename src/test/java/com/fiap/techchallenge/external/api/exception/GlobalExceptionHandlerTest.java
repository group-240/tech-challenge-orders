package com.fiap.techchallenge.external.api.exception;

import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.InvalidCpfException;
import com.fiap.techchallenge.domain.exception.InvalidEmailException;
import com.fiap.techchallenge.domain.exception.ProductLinkedToOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle DomainException")
    void shouldHandleDomainException() {
        // Arrange
        DomainException exception = new DomainException("Domain error occurred");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleDomainException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Domain error occurred", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should handle InvalidCpfException")
    void shouldHandleInvalidCpfException() {
        // Arrange
        InvalidCpfException exception = new InvalidCpfException("Invalid CPF format");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleInvalidCpfException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Invalid CPF format", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should handle InvalidEmailException")
    void shouldHandleInvalidEmailException() {
        // Arrange
        InvalidEmailException exception = new InvalidEmailException("Invalid email format");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleInvalidEmailException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Invalid email format", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should handle ProductLinkedToOrderException")
    void shouldHandleProductLinkedToOrderException() {
        // Arrange
        ProductLinkedToOrderException exception = new ProductLinkedToOrderException("Product is linked to orders");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleProductLinkedToOrderException(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(409, body.get("status"));
        assertEquals("Product is linked to orders", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }
}