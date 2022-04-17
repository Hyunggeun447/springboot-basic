package org.voucherProject.voucherProject.customer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.voucherProject.voucherProject.customer.entity.Customer;
import org.voucherProject.voucherProject.customer.repository.CustomerRepository;
import org.voucherProject.voucherProject.voucher.entity.FixedAmountVoucher;
import org.voucherProject.voucherProject.voucher.entity.Voucher;
import org.voucherProject.voucherProject.voucher.entity.VoucherType;
import org.voucherProject.voucherProject.voucher.repository.VoucherRepository;
import org.voucherProject.voucherProject.voucher.service.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    VoucherService voucherService;
    @Autowired
    CustomerService customerService;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void clear() {
        voucherRepository.deleteAll();
        customerRepository.deleteAll();

        customer = new Customer(UUID.randomUUID(), "aaa", "aaa@naver.com", "1234");
        voucher = new FixedAmountVoucher(UUID.randomUUID(), 2, customer.getCustomerId());

        customerService.save(customer);
        voucherService.save(voucher);
    }

    Customer customer;
    Voucher voucher;

    @Test
    @DisplayName("Id로 조회")
    void findById() {
        Customer byId = customerService.findById(customer.getCustomerId());
        assertThat(byId.getCustomerId()).isEqualTo(customer.getCustomerId());
    }
    @Test
    @DisplayName("없는Id로 조회")
    void findByVoidId() {
        Assertions.assertThrows(RuntimeException.class,
                () -> customerService.findById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("이름으로 조회")
    void findByName() {
        Customer byName = customerService.findByName("aaa");
        assertThat(byName.getCustomerId()).isEqualTo(customer.getCustomerId());
    }
    @Test
    @DisplayName("없는 이름으로 조회")
    void findByVoidName() {
        Assertions.assertThrows(RuntimeException.class,
                () -> customerService.findByName("bbb"));
    }

    @Test
    @DisplayName("email로 조회")
    void findByEmail() {
        Customer byEmail = customerService.findByEmail("aaa@naver.com");
        assertThat(byEmail.getCustomerId()).isEqualTo(customer.getCustomerId());
    }
    @Test
    @DisplayName("없는 email로 조회")
    void findByVoidEmail() {
        Assertions.assertThrows(RuntimeException.class,
                () -> customerService.findByEmail("bbb@naver.com"));
    }

    @Test
    @DisplayName("전체 조회")
    void findAll() {
        List<Customer> all = customerService.findAll();
        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 바우처 타입을 가진 고객 조회")
    public void findByVoucherType() throws Exception {
        Customer customer2 = new Customer(UUID.randomUUID(), "bbb", "bbb@.com", "1234");
        Customer customer3 = new Customer(UUID.randomUUID(), "ccc", "ccc@.com", "1234");

        customerService.save(customer2);
        customerService.save(customer3);

        Voucher voucher2 = VoucherType.PERCENT.createVoucher(11, customer.getCustomerId());
        Voucher voucher3 = VoucherType.FIXED.createVoucher(12, customer2.getCustomerId());
        Voucher voucher4 = VoucherType.PERCENT.createVoucher(13, customer2.getCustomerId());
        Voucher voucher5 = VoucherType.FIXED.createVoucher(12, customer3.getCustomerId());
        voucherService.save(voucher2);
        voucherService.save(voucher3);
        voucherService.save(voucher4);
        voucherService.save(voucher5);

        List<Customer> byVoucherType1 = customerService.findByVoucherType(VoucherType.FIXED);
        List<Customer> byVoucherType2 = customerService.findByVoucherType(VoucherType.PERCENT);

        assertThat(byVoucherType1.size()).isEqualTo(3);
        assertThat(byVoucherType2.size()).isEqualTo(2);
    }




}
