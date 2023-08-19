package com.example.kafkaproducer.vo;

import lombok.Data;

@Data
public class EffectOrNot { // 광고를 보고 구매한 사람인지 아닌지

    String adId; // ad-101
    String effectiveness; // y, n / 0, 1
    String userId; //
}
