package com.fiap.techchallenge.external.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class CustomerApiClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerApiClient.class);
    private final String apiUrl;
    private final int connectTimeout;
    private final int readTimeout;

    public CustomerApiClient(String apiUrl, int connectTimeout, int readTimeout) {
        this.apiUrl = apiUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        logger.info("CustomerApiClient initialized with URL: {}, connectTimeout: {}ms, readTimeout: {}ms", 
                    apiUrl, connectTimeout, readTimeout);
    }

    public JsonNode fetchCustomerByCpf(String cpf) {
        String traceId = getOrCreateTraceId();
        String fullUrl = apiUrl + "/cpf/" + cpf;
        long startTime = System.currentTimeMillis();
        
        logger.info("[traceId: {}] Fetching customer from external API - CPF: {}, URL: {}", traceId, maskCpf(cpf), fullUrl);
        
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Trace-ID", traceId);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            int statusCode = connection.getResponseCode();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("[traceId: {}] Customer API response - Status: {}, Duration: {}ms", traceId, statusCode, duration);

            if (statusCode == 404) {
                logger.warn("[traceId: {}] Customer not found - CPF: {}, URL: {}", traceId, maskCpf(cpf), fullUrl);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + maskCpf(cpf));
            }

            if (statusCode != 200) {
                String errorBody = readErrorStream(connection);
                logger.error("[traceId: {}] Customer API error - Status: {}, URL: {}, ErrorBody: {}", 
                            traceId, statusCode, fullUrl, errorBody);
                throw new ResponseStatusException(
                    HttpStatus.valueOf(statusCode),
                    String.format("Failed to fetch customer: HTTP %d - %s", statusCode, errorBody)
                );
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(response.toString());
            
            logger.info("[traceId: {}] Customer fetched successfully - CPF: {}, Duration: {}ms", 
                       traceId, maskCpf(cpf), duration);
            
            return result;
            
        } catch (SocketTimeoutException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[traceId: {}] Timeout fetching customer - CPF: {}, URL: {}, Duration: {}ms, ConnectTimeout: {}ms, ReadTimeout: {}ms", 
                        traceId, maskCpf(cpf), fullUrl, duration, connectTimeout, readTimeout, e);
            throw new RuntimeException(
                String.format("Timeout connecting to Customer Service (CPF: %s, Duration: %dms, ConnectTimeout: %dms, ReadTimeout: %dms)", 
                             maskCpf(cpf), duration, connectTimeout, readTimeout), e);
        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[traceId: {}] IO error fetching customer - CPF: {}, URL: {}, Duration: {}ms, Error: {}", 
                        traceId, maskCpf(cpf), fullUrl, duration, e.getMessage(), e);
            throw new RuntimeException(
                String.format("Error connecting to Customer Service (CPF: %s, URL: %s, Error: %s)", 
                             maskCpf(cpf), fullUrl, e.getMessage()), e);
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
    
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***" + cpf.substring(cpf.length() - 3);
    }
    
    private String readErrorStream(HttpURLConnection connection) {
        try {
            if (connection.getErrorStream() != null) {
                Scanner scanner = new Scanner(connection.getErrorStream());
                StringBuilder error = new StringBuilder();
                while (scanner.hasNext()) {
                    error.append(scanner.nextLine());
                }
                scanner.close();
                return error.toString();
            }
        } catch (Exception e) {
            logger.debug("Could not read error stream: {}", e.getMessage());
        }
        return "";
    }
}