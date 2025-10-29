package com.fiap.techchallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite todas as requisições sem login
            )
            .csrf(csrf -> csrf.disable()) // Desativa CSRF (recomendado apenas para testes)
            .headers(headers -> headers.frameOptions().disable()); // Permite usar H2-console, etc.

        return http.build();
    }
}
