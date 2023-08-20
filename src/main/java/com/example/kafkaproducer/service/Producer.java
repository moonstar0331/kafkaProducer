package com.example.kafkaproducer.service;

import com.example.kafkaproducer.config.KafkaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @Autowired
    KafkaConfig myConfig;

    // Publishing 할 Topic
    String topicName = "defaultTopic";

    private KafkaTemplate<String, Object> kafkaTemplate;

//    @Autowired
//    public Producer(KafkaTemplate kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void pub(String msg) {
        kafkaTemplate = myConfig.kafkaTemplateForGeneral();
        kafkaTemplate.send(topicName, msg);
    }

    public void sendJoinedMsg(String topicNm, Object msg) {
        kafkaTemplate = myConfig.kafkaTemplateForGeneral();
        kafkaTemplate.send(topicNm, msg);
    }

    public void sendMsgForWatchingAdLog(String topicNm, Object msg) {
        kafkaTemplate = myConfig.kafkaTemplateForWatchingAdLog();
        kafkaTemplate.send(topicNm, msg);
    }

    public void sendMsgForPurchaseLog(String topicNm, Object msg) {
        kafkaTemplate = myConfig.kafkaTemplateForPurchaseLog();
        kafkaTemplate.send(topicNm, msg);
    }
}
