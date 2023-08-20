package com.example.kafkaproducer.service;

import com.example.kafkaproducer.vo.EffectOrNot;
import com.example.kafkaproducer.vo.PurchaseLog;
import com.example.kafkaproducer.vo.PurchaseLogOneProduct;
import com.example.kafkaproducer.vo.WatchingAdLog;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdEvaluationService {

    // 광고 데이터는 중복 Join이 될 필요없다. --> Table
    // 광고 이력이 먼저 들어온다.
    // 구매 이력은 상품별로 들어오지 않는다. (복수개의 상품 존재) --> contain
    // 광고에 머문시간이 적어도 10초 이상이어야만 Join 대상이다.
    // 특정 가격 이상의 상품은 Join 에서 제외 (100만원)
    // 광고이력: KTable(AdLog), 구매이력: KTable(PurchaseLogOneProduct)
    // filtering, 형 변환
    // EffectOrNot --> Json 형태로 Topic: AdEvaluationComplete

    @Autowired Producer myProducer;

    @Autowired
    public void buildPipeline(StreamsBuilder sb) {
        JsonSerializer<EffectOrNot> effectSerializer = new JsonSerializer<>();
        JsonSerializer<PurchaseLog> purchaseLogSerializer = new JsonSerializer<>();
        JsonSerializer<WatchingAdLog> watchingAdLogSerializer = new JsonSerializer<>();
        JsonSerializer<PurchaseLogOneProduct> purchaseLogOneProductSerializer = new JsonSerializer<>();

        JsonDeserializer<EffectOrNot> effectDeSerializer = new JsonDeserializer<>(EffectOrNot.class);
        JsonDeserializer<PurchaseLog> purchaseLogDeSerializer = new JsonDeserializer<>(PurchaseLog.class);
        JsonDeserializer<WatchingAdLog> watchingAdLogDeSerializer = new JsonDeserializer<>(WatchingAdLog.class);
        JsonDeserializer<PurchaseLogOneProduct> purchaseLogOneProductDeSerializer = new JsonDeserializer<>(PurchaseLogOneProduct.class);

        Serde<EffectOrNot> effectOrNotSerde = Serdes.serdeFrom(effectSerializer, effectDeSerializer);
        Serde<PurchaseLog> purchaseLogSerde = Serdes.serdeFrom(purchaseLogSerializer, purchaseLogDeSerializer);
        Serde<WatchingAdLog> watchingAdLogSerde = Serdes.serdeFrom(watchingAdLogSerializer, watchingAdLogDeSerializer);
        Serde<PurchaseLogOneProduct> purchaseLogOneProductSerde = Serdes.serdeFrom(purchaseLogOneProductSerializer, purchaseLogOneProductDeSerializer);

        // adLog stream --> table
        KTable<String, WatchingAdLog> adTable = sb.stream("AdLog", Consumed.with(Serdes.String(), watchingAdLogSerde))
                .selectKey((k, v) -> v.getUserId() + "_" + v.getProductId())
                .toTable(Materialized.<String, WatchingAdLog, KeyValueStore<Bytes, byte[]>>as("adStore")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(watchingAdLogSerde));

        KStream<String, PurchaseLog> purchaseLogKStream = sb.stream("PurchaseLog", Consumed.with(Serdes.String(), purchaseLogSerde));

        purchaseLogKStream.foreach((k, v) -> {
            for (Map<String, String> prodInfo: v.getProductInfo()) {
                if(Integer.valueOf(prodInfo.get("price")) < 1000000) {
                    PurchaseLogOneProduct tempVo = new PurchaseLogOneProduct();
                    tempVo.setUserId(v.getUserId());
                    tempVo.setProductId(prodInfo.get("productId"));
                    tempVo.setOrderId(v.getOrderId());
                    tempVo.setPrice(prodInfo.get("price"));
                    tempVo.setPurchasedDt(v.getPurchasedDt());

                    myProducer.sendJoinedMsg("PurchaseLogOneProduct", tempVo);
                    sendNewMsg();
                }
            }
        });

        KTable<String, PurchaseLogOneProduct> purchaseLogOneProductKTable = sb.stream("PurchaseLogOneProduct", Consumed.with(Serdes.String(), purchaseLogOneProductSerde))
                .selectKey((k, v) -> v.getUserId() + "_" + v.getProductId())
                .toTable(Materialized.<String, PurchaseLogOneProduct, KeyValueStore<Bytes, byte[]>>as("purchaseLogStore")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(purchaseLogOneProductSerde));

        ValueJoiner<WatchingAdLog, PurchaseLogOneProduct, EffectOrNot> tableStreamJoiner = (leftValue, rightValue) -> {
            EffectOrNot returnValue = new EffectOrNot();
            returnValue.setUserId(leftValue.getUserId());
            returnValue.setAdId(leftValue.getAdId());
            returnValue.setOrderId(rightValue.getOrderId());
            Map<String, String> tempProdInfo = new HashMap<>();
            tempProdInfo.put("productId", rightValue.getProductId());
            tempProdInfo.put("price", rightValue.getPrice());
            returnValue.setProductInfo(tempProdInfo);
            System.out.println("Joined!");
            return returnValue;
        };

        adTable.join(purchaseLogOneProductKTable, tableStreamJoiner)
                .toStream()
                .to("AdEvaluationComplete",
                        Produced.with(Serdes.String(), effectOrNotSerde));
    }

    public void sendNewMsg() {
        PurchaseLog tempPurchaseLog = new PurchaseLog();
        WatchingAdLog tempWatchingAdLog = new WatchingAdLog();

        // random user Id
        Random random = new Random();
        int randomUidNumber = random.nextInt(9999);
        int randomOrderIdNumber = random.nextInt(9999);
        int randomProdIdNumber = random.nextInt(9999);
        int randomPrice = random.nextInt(90000) + 10000;
        int prodCnt = random.nextInt(9) + 1;
        int watchingTime = random.nextInt(55) + 5;

        // purchaseLog
        tempPurchaseLog.setUserId("uid-" + String.format("%05d", randomUidNumber));
        tempPurchaseLog.setPurchasedDt("20230101070000");
        tempPurchaseLog.setOrderId("od-" + String.format("%05d", randomOrderIdNumber));
        ArrayList<Map<String, String>> tempProdInfo = new ArrayList<>();
        Map<String, String> tempProd = new HashMap<>();
        for(int i=0; i<prodCnt; i++) {
            tempProd.put("productId", "pg-" + String.format("%05d", randomProdIdNumber));
            tempProd.put("price", String.format("%05d", randomPrice));
            tempProdInfo.add(tempProd);
        }
        tempPurchaseLog.setProductInfo(tempProdInfo);

        // watchingAdLog
        tempWatchingAdLog.setUserId("uid-" + String.format("%05d", randomUidNumber));
        tempWatchingAdLog.setProductId("pg-" + String.format("%05d", randomProdIdNumber));
        tempWatchingAdLog.setAdId("ad-" + String.format("%05d",randomProdIdNumber));
        tempWatchingAdLog.setAdType("banner");
        tempWatchingAdLog.setWatchingTime(String.valueOf(watchingTime));
        tempWatchingAdLog.setWatchingDt("20230101070000");

        myProducer.sendMsgForPurchaseLog("PurchaseLog", tempPurchaseLog);
        myProducer.sendMsgForWatchingAdLog("AdLog", tempWatchingAdLog);
    }
}
