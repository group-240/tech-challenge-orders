package com.fiap.techchallenge.external.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class PaymentApiClient {

    private final String baseUrl;

    public PaymentApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String createPayment(Double amount, String description, String method, Integer installments,
                                String email, String documentType, String documentNumber) {
        try {
            URL url = new URL(baseUrl + "/payments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //connection.setRequestMethod("POST");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
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

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    (status >= 200 && status < 300)
                            ? connection.getInputStream()
                            : connection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            String response = reader.lines().collect(Collectors.joining("\n"));
            connection.disconnect();

            if (status < 200 || status >= 300) {
                throw new RuntimeException("Erro na API de pagamento: " + status + " - " + response);
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar API de pagamento", e);
        }
    }
}
