package com.example.kafkaproducer.vo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PurchaseLog { // 구매한 이력

    String orderId; // od-0001
    String userId; // uid-0001
    ArrayList<String> productId; // pg-0001
    String purchasedDt; // 20230819070000
    Long price; // 24000

}
