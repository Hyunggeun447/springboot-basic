package org.prgrms.kdt.voucher.repository;

import org.prgrms.kdt.order.Order;
import org.prgrms.kdt.order.repository.OrderRepository;
import org.prgrms.kdt.voucher.Voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemoryRepository implements VoucherRepository, OrderRepository {

    private final List<Voucher> vouchers = new ArrayList<>();

    public Voucher addVoucher(Voucher voucher) {
        vouchers.add(voucher);
        return voucher;
    }

    public List<Voucher> findByAllVouchers() {
        return vouchers;
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.empty();
    }

    @Override
    public void insert(Order order) {

    }
}
