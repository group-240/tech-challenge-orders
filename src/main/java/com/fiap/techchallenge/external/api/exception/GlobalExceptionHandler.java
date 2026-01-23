package com.fiap.techchallenge.external.api.exception;

import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.InvalidCpfException;
import com.fiap.techchallenge.domain.exception.InvalidEmailException;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.exception.ProductLinkedToOrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(DomainException ex) {
        String traceId = MDC.get("traceId");
        logger.warn("[traceId: {}] Domain exception: {}", traceId, ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, traceId);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        String traceId = MDC.get("traceId");
        logger.warn("[traceId: {}] Not found exception: {}", traceId, ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, traceId);
    }

    @ExceptionHandler(InvalidCpfException.class)
    public ResponseEntity<Object> handleInvalidCpfException(InvalidCpfException ex) {
        String traceId = MDC.get("traceId");
        logger.warn("[traceId: {}] Invalid CPF: {}", traceId, ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, traceId);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Object> handleInvalidEmailException(InvalidEmailException ex) {
        String traceId = MDC.get("traceId");
        logger.warn("[traceId: {}] Invalid email: {}", traceId, ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, traceId);
    }

    @ExceptionHandler(ProductLinkedToOrderException.class)
    public ResponseEntity<Object> handleProductLinkedToOrderException(ProductLinkedToOrderException ex) {
        String traceId = MDC.get("traceId");
        logger.warn("[traceId: {}] Product linked to order: {}", traceId, ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, traceId);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        String traceId = MDC.get("traceId");
        logger.error("[traceId: {}] HTTP exception: {} - {}", traceId, ex.getStatusCode(), ex.getReason(), ex);
        return buildResponse(ex.getReason(), HttpStatus.valueOf(ex.getStatusCode().value()), traceId);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        String traceId = MDC.get("traceId");
        
        // Identificar se é erro de comunicação com serviços externos
        String message = ex.getMessage();
        if (message != null && (message.contains("Customer Service") || message.contains("Payment Service"))) {
            logger.error("[traceId: {}] External service error: {}", traceId, message, ex);
            return buildResponse("Service temporarily unavailable: " + message, HttpStatus.SERVICE_UNAVAILABLE, traceId);
        }
        
        // Erro genérico
        logger.error("[traceId: {}] Unexpected runtime exception: {}", traceId, message, ex);
        return buildResponse("Internal server error: " + message, HttpStatus.INTERNAL_SERVER_ERROR, traceId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        String traceId = MDC.get("traceId");
        logger.error("[traceId: {}] Unexpected exception: {}", traceId, ex.getMessage(), ex);
        return buildResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, traceId);
    }

    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, String traceId) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", message);
        body.put("traceId", traceId);
        return new ResponseEntity<>(body, status);
    }
}
