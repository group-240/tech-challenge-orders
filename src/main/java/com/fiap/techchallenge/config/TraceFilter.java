package com.fiap.techchallenge.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter que adiciona trace ID a todas as requisições para correlação de logs
 */
@Component
public class TraceFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Obtém trace ID do header ou gera um novo
            String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }

            // Adiciona trace ID ao MDC para uso nos logs
            MDC.put(TRACE_ID_MDC_KEY, traceId);
            
            // Adiciona trace ID na resposta
            httpResponse.setHeader(TRACE_ID_HEADER, traceId);

            logger.debug("[traceId: {}] Request: {} {}", traceId, httpRequest.getMethod(), httpRequest.getRequestURI());

            chain.doFilter(request, response);

        } finally {
            // Limpa MDC após processamento
            MDC.clear();
        }
    }
}
