package com.example.kafkaproducer.service;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class StreamService {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Autowired
    public void buildPipeLine(StreamsBuilder sb) {
//        KStream<String, String> myStream = sb.stream("studyKafka", Consumed.with(STRING_SERDE, STRING_SERDE));
//        myStream.print(Printed.toSysOut());
//        myStream.filter((key, value) -> value.contains("freeClass")).to("freeClassList");

        KStream<String, String> leftStream = sb.stream("leftTopic",
                Consumed.with(STRING_SERDE, STRING_SERDE))
                .selectKey((k, v) -> v.substring(0, v.indexOf(":")));
        // key:value --> 1:leftValue
        KStream<String, String> rightStream = sb.stream("rightTopic",
                Consumed.with(STRING_SERDE, STRING_SERDE))
                .selectKey((k, v) -> v.substring(0, v.indexOf(":")));
        // key:value --> 1:rightValue

        leftStream.print(Printed.toSysOut());
        rightStream.print(Printed.toSysOut());

        KStream<String, String> joinedStream = leftStream.join(rightStream,
                (leftValue, rightValue) -> leftValue + "_" + rightValue,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(1)));

        joinedStream.print(Printed.toSysOut());
        joinedStream.to("joinedMsg");
        // 1:leftValue_1:rightValue
    }
}
