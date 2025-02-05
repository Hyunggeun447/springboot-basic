package org.voucherProject.voucherProject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.voucherProject.voucherProject.voucher.controller.SimpleVoucherController;
import org.voucherProject.voucherProject.voucher.controller.dto.VoucherDto;
import org.voucherProject.voucherProject.voucher.entity.VoucherType;
import org.voucherProject.voucherProject.io.Console;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoucherEnrollSystem implements Runnable {

    private final Console console;
    private final SimpleVoucherController voucherController;

    @Override
    public void run() {
        while (true) {
            String inputString = console.input(
                    "Type exit to exit the program.\n" +
                    "Type create to create a new voucher.\n" +
                    "Type list to list all vouchers");
            try {
                InputCommand inputCommand = InputCommand.is(inputString);
                executeSystem(inputCommand);
            } catch (IllegalArgumentException e) {
                console.errorMessage();
            }
        }
    }

    private void executeSystem(InputCommand inputCommand) {
        switch (inputCommand) {
            case EXIT:
                console.endMessage();
                System.exit(0);
                break;
            case LIST:
                voucherController.findAll().stream().forEach(System.out::println);
                break;
            case CREATE:
                String inputVoucherType = console.input("1. FixedAmountVoucher\n2. PercentDiscountVoucher");
                VoucherType voucherType = VoucherType.getVoucherType(Integer.parseInt(inputVoucherType));
                long amount = Long.parseLong(console.input("할인 수치를 입력해주세요"));

                voucherController.createVoucher(VoucherDto.builder()
                        .voucherType(voucherType)
                        .amount(amount)
                        .build());
                console.completeMessage();
                break;
        }
    }
}
