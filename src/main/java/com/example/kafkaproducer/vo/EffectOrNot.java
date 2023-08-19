package com.example.kafkaproducer.vo;

import lombok.Data;

import java.util.Map;

@Data
public class EffectOrNot { // 광고를 보고 구매한 사람인지 아닌지

    String adId; // ad-101
    String userId; //
    String orderId;
    Map<String, String> productInfo;
}
