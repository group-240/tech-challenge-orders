package com.fiap.techchallenge.external.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthCheckRestControllerTest {

    @InjectMocks
    private HealthCheckRestController healthCheckRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckRestController).build();
    }

    @Test
    void testHealthCheck_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("tech-challenge"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void testHealthCheck_ReturnsCorrectStructure() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.service").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.service").isString())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void testHealthCheck_StatusIsAlwaysUP() throws Exception {
        // Act & Assert - Multiple calls should always return UP
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"));
        }
    }

    @Test
    void testHealthCheck_TimestampIsCurrentTime() throws Exception {
        // Arrange
        long beforeCall = System.currentTimeMillis();

        // Act & Assert
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").value(org.hamcrest.Matchers.greaterThanOrEqualTo(beforeCall)));
    }

    @Test
    void testHealthCheck_ServiceNameIsCorrect() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("tech-challenge"));
    }

    @Test
    void testHealthCheck_ResponseContainsAllRequiredFields() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.service").isString())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}