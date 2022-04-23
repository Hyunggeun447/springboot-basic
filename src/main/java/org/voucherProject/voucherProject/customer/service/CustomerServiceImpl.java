package org.voucherProject.voucherProject.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.voucherProject.voucherProject.customer.entity.Customer;
import org.voucherProject.voucherProject.customer.repository.CustomerDao;
import org.voucherProject.voucherProject.voucher.entity.VoucherType;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerRepository;

    @Override
    public Customer findById(UUID customerId) {
        try {
            return customerRepository.findById(customerId).get();
        } catch (RuntimeException e) {
            log.info("해당 id를 가지는 고객이 존재하지 않습니다.");
            throw new RuntimeException();
        }
    }

    @Override
    public Customer findByName(String customerName) {
        return customerRepository.findByName(customerName)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @Override
    public Customer findByEmail(String customerEmail) {
        return customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @Override
    public List<Customer> findByVoucherType(VoucherType voucherType) {
        return customerRepository.findByVoucherType(voucherType);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Customer customer) {
        return customerRepository.update(customer);
    }

    @Override
    public void deleteAll() {
        customerRepository.deleteAll();
    }
}
