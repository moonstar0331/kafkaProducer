package com.example.kafkaproducer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<String, Object>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> myConfig = new HashMap<>();
        myConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "43.201.66.101:9092, 43.201.101.8:9092, 43.200.177.54:9092");
        myConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        myConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(myConfig);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> myConfig = new HashMap<>();
        myConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "43.201.66.101:9092, 43.201.101.8:9092, 43.200.177.54:9092");
        myConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        myConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(myConfig);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> myFactory = new ConcurrentKafkaListenerContainerFactory<>();
        myFactory.setConsumerFactory(consumerFactory());
        return myFactory;
    }
}
