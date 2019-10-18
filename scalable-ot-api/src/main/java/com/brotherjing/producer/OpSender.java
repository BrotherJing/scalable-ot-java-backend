package com.brotherjing.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.brotherjing.Const;
import com.brotherjing.proto.TextProto;

@Component
public class OpProducer {

    @Autowired
    private KafkaTemplate<String, TextProto.Command> kafkaTemplate;

    public void put(TextProto.Command command) {
        kafkaTemplate.send(Const.TOPIC_OP, command);
    }
}
