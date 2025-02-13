package org.voucherProject.voucherProject.customer.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.voucherProject.voucherProject.customer.entity.Customer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * customer_blacklist.csv 파일을 만들고 스프링 애플리케이션에서 해당 파일을 읽을 수 있고 블랙 리스트조회 할 수있다
 */
@Repository
@Slf4j
public class BlackListFileRepository {

    private final String BLACKLIST_LIST_PATH = "src/main/resources/customer_blacklist.csv";

    private List<Customer> getVouchers(){
        List<Customer> blackList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(BLACKLIST_LIST_PATH));
            String readLine = null;
            while ((readLine = bufferedReader.readLine()) != null) {
                String[] readLineSplit = readLine.split(",");
                blackList.add(new Customer(UUID.fromString(readLineSplit[0]), readLineSplit[1], readLineSplit[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blackList;
    }
}
