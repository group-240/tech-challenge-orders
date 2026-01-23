package com.fiap.techchallenge.external.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentApiClient.class);
    private final String baseUrl;
    private final int connectTimeout;
    private final int readTimeout;

    public PaymentApiClient(String baseUrl, int connectTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        logger.info("PaymentApiClient initialized with URL: {}, connectTimeout: {}ms, readTimeout: {}ms", 
                    baseUrl, connectTimeout, readTimeout);
    }

    public String createPayment(Double amount, String description, String method, Integer installments,
                                String email, String documentType, String documentNumber) {
        String traceId = getOrCreateTraceId();
        String fullUrl = baseUrl + "/payments";
        long startTime = System.currentTimeMillis();
        
        logger.info("[traceId: {}] Creating payment request - Amount: {}, Method: {}, Email: {}, Document: {}***", 
                    traceId, amount, method, email, maskDocument(documentNumber));
        
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET"); // Mantido GET conforme código original
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Trace-ID", traceId);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);

            String payload = String.format("""
                {
                    "amount": %.2f,
                    "description": "%s",
                    "payment_method_id": "%s",
                    "installments": %d,
                    "payer": {
                        "email": "%s",
                        "identification": {
                            "type": "%s",
                            "number": "%s"
                        }
                    }
                }
            """, amount, description, method, installments, email, documentType, documentNumber);

            logger.debug("[traceId: {}] Payment payload prepared - Size: {} bytes", traceId, payload.length());

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info("[traceId: {}] Payment API response - Status: {}, Duration: {}ms", traceId, status, duration);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    (status >= 200 && status < 300)
                            ? connection.getInputStream()
                            : connection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            String response = reader.lines().collect(Collectors.joining("\n"));

            if (status < 200 || status >= 300) {
                logger.error("[traceId: {}] Payment API error - Status: {}, URL: {}, Duration: {}ms, Response: {}", 
                            traceId, status, fullUrl, duration, response);
                throw new RuntimeException(
                    String.format("Payment API failed: HTTP %d (Duration: %dms) - %s", status, duration, response));
            }

            logger.info("[traceId: {}] Payment created successfully - Duration: {}ms, ResponseSize: {} bytes", 
                       traceId, duration, response.length());
            
            return response;

        } catch (SocketTimeoutException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[traceId: {}] Timeout creating payment - URL: {}, Duration: {}ms, ConnectTimeout: {}ms, ReadTimeout: {}ms", 
                        traceId, fullUrl, duration, connectTimeout, readTimeout, e);
            throw new RuntimeException(
                String.format("Timeout connecting to Payment Service (Duration: %dms, ConnectTimeout: %dms, ReadTimeout: %dms)", 
                             duration, connectTimeout, readTimeout), e);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[traceId: {}] Error creating payment - URL: {}, Duration: {}ms, Error: {}", 
                        traceId, fullUrl, duration, e.getMessage(), e);
            throw new RuntimeException(
                String.format("Error calling Payment API (URL: %s, Error: %s)", fullUrl, e.getMessage()), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private String getOrCreateTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        return traceId;
    }
    
    private String maskDocument(String document) {
        if (document == null || document.length() < 4) {
            return "***";
        }
        return document.substring(document.length() - 3);
    }
}
