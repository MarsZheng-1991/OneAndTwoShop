package com.OneAndTwoShop.orderService.util;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderNumberGenerator {

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String currentDate = LocalDate.now().format(DATE_FMT);
    private AtomicInteger counter = new AtomicInteger(0);

    public synchronized String generateOrderNo() {
        String today = LocalDate.now().format(DATE_FMT);
        if (!today.equals(currentDate)) {
            currentDate = today;
            counter.set(0);
        }
        int seq = counter.incrementAndGet(); // 001, 002...
        return PREFIX + today + String.format("%03d", seq);
    }
}