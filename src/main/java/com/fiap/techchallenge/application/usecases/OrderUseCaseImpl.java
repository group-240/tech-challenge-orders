package com.fiap.techchallenge.application.usecases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.domain.entities.*;
import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.repositories.OrderRepository;
import com.fiap.techchallenge.domain.repositories.ProductRepository;
import com.fiap.techchallenge.external.api.CustomerApiClient;
import com.fiap.techchallenge.external.api.PaymentApiClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class OrderUseCaseImpl implements OrderUseCase {

    public static final String RECORD_NOT_FOUND_MESSAGE = "Record not found";

    public final OrderRepository orderRepository;
    public final ProductRepository productRepository;
    public final CustomerApiClient customerApiClient;
    private final PaymentApiClient paymentApiClient;

    public OrderUseCaseImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            CustomerApiClient customerApiClient,
                            PaymentApiClient paymentApiClient) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerApiClient = customerApiClient;
        this.paymentApiClient = paymentApiClient;
    }

    @Override
    public Order createOrder(String cpf, List<OrderItemRequest> items) {
        JsonNode customerData = customerApiClient.fetchCustomerByCpf(cpf);
        List<OrderItem> orderItems = validateAndConvertOrderItems(items);
        return createAndSaveOrder(customerData, orderItems);
    }

    public List<OrderItem> validateAndConvertOrderItems(List<OrderItemRequest> items) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : items) {
            validateQuantity(itemRequest.getQuantity());
            Product product = validateProduct(itemRequest.getProductId());
            OrderItem orderItem = OrderItem.create(product, itemRequest.getQuantity());
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public Product validateProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!product.isActive()) {
            throw new DomainException("Product is not active: " + product.getName());
        }

        return product;
    }

    public void validateQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new DomainException("Quantity must be greater than zero");
        }
    }

    public Order createAndSaveOrder(JsonNode customerData, List<OrderItem> orderItems) {
        String cpf = (customerData != null && customerData.has("cpf")) ? customerData.get("cpf").asText() : null;

        Order order = Order.create(cpf, orderItems);
        order.setStatus(OrderStatus.RECEIVED);
        order.setStatusPayment(StatusPayment.AGUARDANDO_PAGAMENTO);

        Long idPayment = createPaymentOrder(order, customerData);
        order.setIdPayment(idPayment);

        return orderRepository.save(order);
    }

    public Long createPaymentOrder(Order order, JsonNode customerData) {
        Double amount = order.getTotalAmount().doubleValue();
        String description = "Pagamento para o pedido";
        String paymentMethodId = "pix";
        Integer installments = 1;

        String email = (customerData != null && customerData.has("email")) ? customerData.get("email").asText() : "default@example.com";
        String cpf = (customerData != null && customerData.has("cpf")) ? customerData.get("cpf").asText() : "00000000000";

        String response = paymentApiClient.createPayment(
            amount, description, paymentMethodId, installments, email, "CPF", cpf
        );

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            return root.path("id").asLong();
        } catch (IOException e) {
            throw new RuntimeException("Error parsing payment response", e);
        }
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new NotFoundException(RECORD_NOT_FOUND_MESSAGE);
        }
        return order;
    }

    @Override
    public List<Order> findByOptionalStatus(OrderStatus status) {
        return orderRepository.findByOptionalStatus(status);
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(RECORD_NOT_FOUND_MESSAGE));

        if (!existingOrder.getStatusPayment().equals(StatusPayment.APROVADO)) {
            throw new DomainException("The order is not paid");
        }

        existingOrder.setStatus(status);
        existingOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(existingOrder);
    }

    @Override
    public Order updateOrderStatus(Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(RECORD_NOT_FOUND_MESSAGE));

        existingOrder.setStatus(OrderStatus.IN_PREPARATION);
        existingOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(existingOrder);
    }

    @Override
    public Order updateOrderStatusPayment(Long id, StatusPayment statusPayment) {
        Order existingOrder = orderRepository.findByIdPayment(id)
                .orElseThrow(() -> new NotFoundException(RECORD_NOT_FOUND_MESSAGE));

        existingOrder.setStatusPayment(statusPayment);
        existingOrder.setStatus(OrderStatus.IN_PREPARATION);
        existingOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(existingOrder);
    }
}
