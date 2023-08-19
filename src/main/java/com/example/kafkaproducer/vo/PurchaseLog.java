package com.example.kafkaproducer.vo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PurchaseLog { // 구매한 이력

    // { "orderId": "od-0003", "userId":"uid-0007", "productId" ["pg-0007", "pg-0007"], "purchasedDt":"20230210700000", "price":24000}

    String orderId; // od-0001
    String userId; // uid-0001
    ArrayList<String> productId; // {pg-0001, pg-0002}
    String purchasedDt; // 20230819070000
    Long price; // 24000

}
