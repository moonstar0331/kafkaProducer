package com.example.kafkaproducer.vo;

import lombok.Data;

@Data
public class PurchaseLogOneProduct {

    String orderId; // od-0001
    String userId; // uid-0001
    String productId; // pg-0001
    String purchasedDt; // 20230819070000
    Long price; // 24000
}
