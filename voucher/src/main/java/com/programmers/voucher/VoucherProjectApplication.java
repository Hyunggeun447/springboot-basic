package com.programmers.voucher;

import com.programmers.voucher.config.ApplicationMessages;
import com.programmers.voucher.entity.customer.Customer;
import com.programmers.voucher.entity.voucher.DiscountPolicy;
import com.programmers.voucher.entity.voucher.Voucher;
import com.programmers.voucher.service.customer.CustomerService;
import com.programmers.voucher.service.customer.CustomerVoucherService;
import com.programmers.voucher.service.voucher.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootApplication
public class VoucherProjectApplication {

    private static ApplicationContext applicationContext;
    private static ApplicationMessages applicationMessages;
    private static CustomerVoucherService customerVoucherService;
    private static VoucherService voucherService;
    private static CustomerService blacklistCustomerService;
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    private static final Logger log = LoggerFactory.getLogger(VoucherProjectApplication.class);

    public static String acquireInput(String msg) {
        try {
            bw.write(msg);
            bw.flush();
            return br.readLine().strip();
        } catch (IOException ex) {
            log.warn("IOException occur when reading user input.");
            return "";
        }
    }

    public static void printOutput(String delim, Object... args) {
        try {
            for (Object arg : args) {
                bw.write(arg.toString());
                if("\n".equals(delim) || "\r\n".equals(delim)) {
                    bw.newLine();
                } else {
                    bw.write(delim);
                }
            }

            bw.flush();
        } catch (IOException ex) {
            log.warn("IOException occur when printing output.");
        }
    }

    public enum Command {
        CREATE_VOUCHER("create_voucher", () -> {
            String voucherName = "";
            while (voucherName.isBlank()) {
                voucherName = acquireInput(applicationMessages.getRequireName());
            }

            String voucherTypeInput = "";
            while (voucherTypeInput.isBlank()) {
                voucherTypeInput = acquireInput(applicationMessages.getRequireType());
            }
            DiscountPolicy.Type voucherType = DiscountPolicy.Type.of(voucherTypeInput);

            int voucherAmount = 0;
            boolean allowed = false;
            while (!allowed) {
                String voucherAmountInput = "";
                while (voucherAmountInput.isBlank()) {
                    voucherAmountInput = acquireInput(applicationMessages.getRequireAmount());
                }

                try {
                    voucherAmount = voucherType.constraint(Integer.parseInt(voucherAmountInput));
                    allowed = true;
                } catch (NumberFormatException ex) {
                    log.warn("Invalid number format for voucher amount.");
                }
            }

            long customerId = 0;
            allowed = false;
            while (!allowed) {
                String customerIdInput = "";
                while (customerIdInput.isBlank()) {
                    customerIdInput = acquireInput(applicationMessages.getRequireCustomerId());
                }

                try {
                    customerId = Long.parseLong(customerIdInput);
                    allowed = true;
                } catch (NumberFormatException ex) {
                    log.warn("Invalid number format for customer id.");
                }
            }

            printOutput("", voucherService.create(voucherName, voucherType, voucherAmount, customerId));
        }),
        LIST_VOUCHER("list_voucher", () -> {
            printOutput("\n", "======= [ VOUCHERS ] =======");
            voucherService.listAll().forEach(voucher -> printOutput("\n", voucher));
            printOutput("\n", "============================");
        }),
        READ_VOUCHER("read_voucher", () -> {
            long voucherId = 0;
            boolean allowed = false;
            while (!allowed) {
                String voucherIdInput = "";
                while (voucherIdInput.isBlank()) {
                    voucherIdInput = acquireInput(applicationMessages.getRequireVoucherId());
                }

                try {
                    voucherId = Long.parseLong(voucherIdInput);
                    allowed = true;
                } catch (NumberFormatException ex) {
                    log.warn("Invalid number format for customer id.");
                }
            }

            voucherService.findById(voucherId).ifPresentOrElse(
                    voucher -> printOutput("", voucher),
                    () -> printOutput("", "NO VOUCHER FOUND."));
        }),
        UPDATE_VOUCHER("update_voucher", () -> {
            long voucherId = 0;
            boolean allowed = false;
            while (!allowed) {
                String voucherIdInput = "";
                while (voucherIdInput.isBlank()) {
                    voucherIdInput = acquireInput(applicationMessages.getRequireVoucherId());
                }

                try {
                    voucherId = Long.parseLong(voucherIdInput);
                    allowed = true;
                } catch (NumberFormatException ex) {
                    log.warn("Invalid number format for voucher id.");
                }
            }

            voucherService.findById(voucherId).ifPresentOrElse(
                    voucher -> {
                        String updateTypeInput = "";
                        while (updateTypeInput.isBlank()) {
                            updateTypeInput = acquireInput(applicationMessages.getRequireUpdateField());
                        }

                        String updateValueInput = "";
                        while(updateValueInput.isBlank()) {
                            updateValueInput = acquireInput(applicationMessages.getRequireUpdateValue());
                        }

                        voucher.update(Voucher.UpdatableField.of(updateTypeInput), updateValueInput);
                        voucherService.update(voucher);
                    },
                    () -> printOutput("", "NO VOUCHER FOUND.")
            );
        }),
        DELETE_VOUCHER("delete_voucher", () -> {
            try {
                System.out.print(applicationMessages.getRequireVoucherId());
                long voucherId = Integer.parseInt(br.readLine());
                voucherService.delete(voucherId);
            } catch (IOException ex) {
                log.warn("IOException occur when input voucher id. {}...", ex.getMessage());
            }
        }),
        BLACKLIST("blacklist", () -> {
            System.out.println("====== [ BLACKLIST ] ======");
            blacklistCustomerService.listAll().forEach(System.out::println);
            System.out.println("===========================");
        }),
        INTRO("intro", () -> {
            System.out.println(applicationMessages.getIntroMessage());
        }),
        UNKNOWN("unknown", () -> {
            // do nothing
        }),
        CREATE_USER("create_user", () -> {
            boolean allowed = false;
            while(!allowed) {
                String usernameInput = "";
                while(usernameInput.isBlank()) {
                    usernameInput = acquireInput("Type username: ");
                }

                String aliasInput = "";
                while(aliasInput.isBlank()) {
                    aliasInput = acquireInput("Type alias: ");
                }
                try {
                    printOutput("", customerVoucherService.create(usernameInput, aliasInput));
                    allowed = true;
                } catch (DuplicateKeyException ex) {
                    log.warn("Duplicated username. Please try another username.");
                }
            }
        }),
        LIST_USER("list_user", () -> {
            System.out.println("======== [ USERS ] ========");
            customerVoucherService.listAll().forEach(System.out::println);
            System.out.println("===========================");
        }),
        LIST_USER_VOUCHER("list_user_voucher", () -> {
            try {
                System.out.print(applicationMessages.getRequireCustomerId());
                long id = Long.parseLong(br.readLine());
                final List<Voucher> allByCustomer = customerVoucherService.findAllVoucherByCustomer(id);
                System.out.println("==== [ VOUCHERS ] ====");
                allByCustomer.forEach(System.out::println);
                System.out.println("======================");

            } catch (IOException ex) {
                log.warn("IOException occur when input customer id. {}...", ex.getMessage());
            }
        }),
        READ_USER_BY_VOUCHER("read_user_by_voucher", () -> {
            try {
                System.out.print(applicationMessages.getRequireVoucherId());
                long id = Long.parseLong(br.readLine());
                customerVoucherService.findCustomerByVoucher(id).ifPresentOrElse(
                        System.out::println,
                        () -> {
                            System.out.println("NO CUSTOMER FOUND.");
                        }
                );
            } catch (IOException ex) {
                log.warn("IOException occur when input customer id. {}...", ex.getMessage());
            }
        }),
        TEST("test", () -> {
            voucherService.listAll().forEach(voucher -> {
                int discounted = voucher.getDiscountPolicy().discount(5000);
                System.out.printf("discounted %d to %d using %s.%n", 5000, discounted, voucher);
            });
        });

        String name;
        Runnable behavior;

        Command(String name, Runnable behavior) {
            this.name = name;
            this.behavior = behavior;
        }

        public static Command of(String input) {
            try {
                return Command.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return Command.UNKNOWN;
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws IOException {
        applicationContext = SpringApplication.run(VoucherProjectApplication.class);

        voucherService = applicationContext.getBean(VoucherService.class);
        voucherService.openStorage();

        customerVoucherService = applicationContext.getBean("basicCustomerService", CustomerVoucherService.class);
        customerVoucherService.openStorage();

        blacklistCustomerService = applicationContext.getBean("blacklistCustomerService", CustomerService.class);
        blacklistCustomerService.openStorage();
        Command.BLACKLIST.behavior.run();

        applicationMessages = applicationContext.getBean(ApplicationMessages.class);

        for (String input = ""; !"exit".equals(input); input = br.readLine()) {
            Command command = Command.of(input);
            try {
                command.behavior.run();
            } catch (UnsupportedOperationException ex) {
                System.out.println("UNSUPPORTED COMMAND");
            }
            Command.INTRO.behavior.run();
        }

        voucherService.closeStorage();
    }
}
