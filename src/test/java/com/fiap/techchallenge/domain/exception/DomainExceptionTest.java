package com.fiap.techchallenge.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    @DisplayName("Should create DomainException with message")
    void shouldCreateDomainExceptionWithMessage() {
        // Arrange
        String message = "Test domain exception";

        // Act
        DomainException exception = new DomainException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create DomainException with message and cause")
    void shouldCreateDomainExceptionWithMessageAndCause() {
        // Arrange
        String message = "Test domain exception";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        DomainException exception = new DomainException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}