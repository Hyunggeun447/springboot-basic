package org.voucherProject.voucherProject.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.voucherProject.voucherProject.voucher.entity.Voucher;
import org.voucherProject.voucherProject.voucher.controller.dto.VoucherDto;
import org.voucherProject.voucherProject.voucher.service.VoucherService;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SimpleVoucherController {

    private final VoucherService voucherService;

    public Voucher createVoucher(VoucherDto voucherDto) {
        UUID customerId = voucherDto.getCustomerId();
        long amount = voucherDto.getAmount();
        Voucher voucher = voucherDto.getVoucherType().createVoucher(amount, customerId);
        return voucherService.save(voucher);
    }

    public List<VoucherDto> findAll() {
        List<Voucher> vouchers = voucherService.findAll();
        return vouchers.stream()
                .map(v -> VoucherDto.builder()
                        .voucherType(v.getVoucherType())
                        .amount(v.getHowMuch())
                        .voucherStatus(v.getVoucherStatus())
                        .build())
                .toList();
    }

    public void useVoucher(VoucherDto voucherDto) {
        Voucher voucher = voucherService.findById(voucherDto.getVoucherId());
        voucherService.useVoucher(voucher);
    }

    public VoucherDto findById(VoucherDto voucherDto) {
        Voucher voucher = voucherService.findById(voucherDto.getVoucherId());
        return VoucherDto.builder()
                .voucherType(voucher.getVoucherType())
                .voucherStatus(voucher.getVoucherStatus())
                .amount(voucher.getHowMuch())
                .build();
    }
}
