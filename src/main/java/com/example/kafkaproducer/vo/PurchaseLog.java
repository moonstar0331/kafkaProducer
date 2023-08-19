package com.example.kafkaproducer.vo;

import lombok.Data;

@Data
public class PurchaseLog { // 구매한 이력

    String userId; // uid-0001
    String productId; // pg-0001
    String purchaseDt; // 20230819070000
    Long price; // 24000

}
