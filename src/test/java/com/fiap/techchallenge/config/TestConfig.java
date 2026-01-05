package com.fiap.techchallenge.config;

/**
 * Test configuration class to centralize test environment variables and constants.
 * This ensures consistency across all test classes and makes it easier to manage test configurations.
 */
public class TestConfig {
    
    // API URLs
    public static final String CUSTOMER_API_BASE_URL = getEnvOrDefault("CUSTOMER_API_BASE_URL", "https://api.test.com");
    public static final String PAYMENT_API_BASE_URL = getEnvOrDefault("PAYMENT_API_BASE_URL", "http://localhost:9090");
    public static final String MERCADO_PAGO_ACCESS_TOKEN = getEnvOrDefault("MERCADO_PAGO_ACCESS_TOKEN", "TEST-123456");
    
    // Test ports for different scenarios
    public static final String PAYMENT_API_PORT_SUCCESS = getEnvOrDefault("PAYMENT_API_PORT_SUCCESS", "9090");
    public static final String PAYMENT_API_PORT_INVALID = getEnvOrDefault("PAYMENT_API_PORT_INVALID", "59999");
    public static final String PAYMENT_API_PORT_NULL_VALUES = getEnvOrDefault("PAYMENT_API_PORT_NULL_VALUES", "59998");
    public static final String PAYMENT_API_PORT_JSON_FORMAT = getEnvOrDefault("PAYMENT_API_PORT_JSON_FORMAT", "59997");
    public static final String PAYMENT_API_PORT_SPECIAL_CHARS = getEnvOrDefault("PAYMENT_API_PORT_SPECIAL_CHARS", "59996");
    public static final String PAYMENT_API_PORT_INSTALLMENTS = getEnvOrDefault("PAYMENT_API_PORT_INSTALLMENTS", "59995");
    
    // Test data constants
    public static final String TEST_CPF = "12345678901";
    public static final String TEST_EMAIL = "test@test.com";
    public static final String INVALID_URL = "invalid-url";
    
    /**
     * Helper method to get environment variable or return default value
     * @param envVar Environment variable name
     * @param defaultValue Default value if env var is not set
     * @return Environment variable value or default
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get full URL for customer API
     * @return Customer API URL
     */
    public static String getCustomerApiUrl() {
        return CUSTOMER_API_BASE_URL;
    }
    
    /**
     * Get full URL for payment API
     * @param port Port number
     * @return Payment API URL with port
     */
    public static String getPaymentApiUrl(String port) {
        return "http://localhost:" + port;
    }
    
    /**
     * Get full URL for payment API with default port
     * @return Payment API URL
     */
    public static String getPaymentApiUrl() {
        return PAYMENT_API_BASE_URL;
    }
    
    /**
     * Get Mercado Pago access token
     * @return Access token
     */
    public static String getMercadoPagoAccessToken() {
        return MERCADO_PAGO_ACCESS_TOKEN;
    }
}
