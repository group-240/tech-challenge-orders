package com.fiap.techchallenge.application.usecases;

import com.fiap.techchallenge.domain.entities.Customer;
import com.fiap.techchallenge.domain.exception.DomainException;
import com.fiap.techchallenge.domain.exception.NotFoundException;
import com.fiap.techchallenge.domain.repositories.CustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerUseCaseImpl implements CustomerUseCase {

    private static final String RECORD_NOT_FOUND_MESSAGE = "Record not found";

    private final CustomerRepository customerRepository;

    public CustomerUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer registerCustomer(String name, String email, String cpf) {
        if (customerRepository.existsByCpf(cpf)) {
            throw new DomainException("Customer with CPF " + cpf + " already exists");
        }

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .cpf(cpf)
                .build();

        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findCustomerByCpf(String cpf) {
        Optional<Customer> customer = customerRepository.findByCpf(cpf);
        if (customer.isEmpty()) {
            throw new NotFoundException(RECORD_NOT_FOUND_MESSAGE);
        }
        return customer;
    }

    @Override
    public Optional<Customer> findCustomerById(UUID id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new NotFoundException(RECORD_NOT_FOUND_MESSAGE);
        }
        return customer;
    }

    @Override
    public List<Customer> findCustomerAll() {
        return customerRepository.findAll();
    }
}
