package com.example.kafkaproducer.service;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamService {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Autowired
    public void buildPipeLine(StreamsBuilder sb) {
        KStream<String, String> myStream = sb.stream("studyKafka", Consumed.with(STRING_SERDE, STRING_SERDE));
        myStream.print(Printed.toSysOut());
        myStream.filter((key, value) -> value.contains("freeClass")).to("freeClassList");
    }
}
