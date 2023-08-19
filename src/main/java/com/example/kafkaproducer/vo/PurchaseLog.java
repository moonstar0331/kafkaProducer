package com.example.kafkaproducer.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class PurchaseLog { // 구매한 이력

    // { "orderId": "od-0005", "userId":"uid-0005", "productInfo": [{"productId": "pg-0023", "price": "12000"}, {"productId": "pg-0022", "price": "13500"}], "purchasedDt":"20230210700000"}

    String orderId; // od-0001
    String userId; // uid-0001
//    ArrayList<String> productId; // {pg-0001, pg-0002}
    ArrayList<Map<String, String>> productInfo; // {["productId":"pg-0001", "price":"24000"]}
    String purchasedDt; // 20230819070000
//    Long price; // 24000

}
