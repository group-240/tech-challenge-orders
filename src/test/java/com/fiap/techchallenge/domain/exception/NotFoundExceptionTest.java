package com.fiap.techchallenge.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    @DisplayName("Should create NotFoundException with message")
    void shouldCreateNotFoundExceptionWithMessage() {
        // Arrange
        String message = "Record not found";

        // Act
        NotFoundException exception = new NotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create NotFoundException with message and cause")
    void shouldCreateNotFoundExceptionWithMessageAndCause() {
        // Arrange
        String message = "Record not found";
        Throwable cause = new RuntimeException("Database error");

        // Act
        NotFoundException exception = new NotFoundException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}