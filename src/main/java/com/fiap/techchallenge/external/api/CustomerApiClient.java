package com.fiap.techchallenge.external.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CustomerApiClient {

    private final String apiUrl;

    public CustomerApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public JsonNode fetchCustomerByCpf(String cpf) {
        try {
            String fullUrl = apiUrl + "/cpf/" + cpf;
            HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

             int statusCode = connection.getResponseCode();

            if (statusCode == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found " + cpf);
            }

            if (connection.getResponseCode() != 200) {
                throw new NotFoundException("Customer not found: HTTP " + connection.getResponseCode());
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error fetching customer data", e);
        }
    }
}