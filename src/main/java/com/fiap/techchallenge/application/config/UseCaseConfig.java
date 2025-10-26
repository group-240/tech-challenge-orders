package com.fiap.techchallenge.application.config;

import com.fiap.techchallenge.adapters.controllers.*;
import com.fiap.techchallenge.adapters.gateway.*;
import com.fiap.techchallenge.application.usecases.*;
import com.fiap.techchallenge.domain.repositories.*;
import com.fiap.techchallenge.external.api.CustomerApiClient;
import com.fiap.techchallenge.external.datasource.repositories.*;
import com.fiap.techchallenge.external.datasource.mercadopago.MercadoPagoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    // Repository Gateways (implementam as interfaces do domínio)
    @Bean
    public CategoryRepository categoryRepository(CategoryJpaRepository categoryJpaRepository) {
        return new CategoryRepositoryGateway(categoryJpaRepository);
    }

    @Bean
    public ProductRepository productRepository(ProductJpaRepository productJpaRepository) {
        return new ProductRepositoryGateway(productJpaRepository);
    }

    @Bean
    public OrderRepository orderRepository(OrderJpaRepository orderJpaRepository) {
        return new OrderRepositoryGateway(orderJpaRepository);
    }

    @Bean
    public PaymentRepository paymentRepository(MercadoPagoClient mercadoPagoClient) {
        return new PaymentRepositoryGateway(mercadoPagoClient);
    }

    // Use Cases (aplicação core)
    @Bean
    public CategoryUseCase categoryUseCase(CategoryRepository categoryRepository, ProductRepository productRepository) {
        return new CategoryUseCaseImpl(categoryRepository, productRepository);
    }

    @Bean
    public ProductUseCase productUseCase(ProductRepository productRepository,
                                        CategoryRepository categoryRepository,
                                        OrderRepository orderRepository) {
        return new ProductUseCaseImpl(productRepository, categoryRepository, orderRepository);
    }

    @Bean
    public OrderUseCase orderUseCase(OrderRepository orderRepository,
                                    ProductRepository productRepository,
                                    PaymentRepository paymentRepository,
                                    CustomerApiClient customerApiClient) {
        return new OrderUseCaseImpl(orderRepository, productRepository, paymentRepository, customerApiClient);
    }

    @Bean
    public PaymentUseCase paymentUseCase(PaymentRepository paymentRepository) {
        return new PaymentUseCaseImpl(paymentRepository);
    }

    @Bean
    public PaymentNotificationUseCase paymentNotificationUseCase(OrderUseCase orderUseCase) {
        return new PaymentNotificationUseCaseImpl(orderUseCase);
    }

    @Bean
    public CustomerApiClient customerApiClient(@Value("${customer-api.base-url}") String baseUrl) {
        return new CustomerApiClient(baseUrl);
    }

    // Controllers de orquestração (adapters)
    @Bean
    public CategoryController categoryController(CategoryUseCase categoryUseCase) {
        return new CategoryController(categoryUseCase);
    }

    @Bean
    public ProductController productController(ProductUseCase productUseCase) {
        return new ProductController(productUseCase);
    }

    @Bean
    public OrderController orderController(OrderUseCase orderUseCase) {
        return new OrderController(orderUseCase);
    }

    @Bean
    public PaymentController paymentController(PaymentUseCase paymentUseCase) {
        return new PaymentController(paymentUseCase);
    }

    @Bean
    public WebhookController webhookController(PaymentNotificationUseCase paymentNotificationUseCase) {
        return new WebhookController(paymentNotificationUseCase);
    }
}
