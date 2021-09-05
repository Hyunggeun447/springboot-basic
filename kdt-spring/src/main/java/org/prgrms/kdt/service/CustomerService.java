package org.prgrms.kdt.service;

import org.prgrms.kdt.domain.customer.Customer;
import org.prgrms.kdt.repository.customer.CustomerRepository;
import org.prgrms.kdt.repository.wallet.WalletRepository;
import org.prgrms.kdt.service.dto.RequestCreateCustomerDto;
import org.prgrms.kdt.service.dto.RequestUpdateCustomerDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }

    @Transactional
    public Customer insert(RequestCreateCustomerDto dto) {
        return customerRepository.insert(dto.toEntity(UUID.randomUUID()));
    }

    public Customer findById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Can not find customer with id = " + customerId));
    }

    public List<Customer> customers() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer update(RequestUpdateCustomerDto dto) {
        Customer customer = findById(dto.getCustomerId());
        changeCustomerName(customer, dto.getName());
        customerRepository.update(customer);
        return customer;
    }

    public Customer login(UUID customerId) {
        Customer customer = findById(customerId);
        login(customer);
        return customer;
    }

    @Transactional
    public void delete(UUID customerId) {
        customerRepository.deleteById(customerId);
    }

    private void login(Customer customer) {
        customer.login();
        customerRepository.update(customer);
    }

    private void changeCustomerName(Customer customer, String name) {
        customer.changeName(name);
    }
}
