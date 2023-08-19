package com.example.kafkaproducer.vo;

import lombok.Data;

@Data
public class WatchingAdLog { // 광고를 본 이력

    String userId; // uid-0001
    String productId; // pg-0001
    String adId; // ad-101
    String adType; // banner, clip,  main, live
    String watchingDt; // 20230819090000
}
