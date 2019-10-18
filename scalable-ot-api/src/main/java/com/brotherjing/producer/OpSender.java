package com.brotherjing.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.brotherjing.Const;
import com.brotherjing.proto.TextProto;

/**
 * Send user's operations through the op topic,
 * which will be processed by the OT module sequentially.
 */
@Component
public class OpSender {

    @Autowired
    private KafkaTemplate<String, TextProto.Command> kafkaTemplate;

    public void send(String docId, TextProto.Command command) {
        kafkaTemplate.send(Const.TOPIC_OP, docId, command);
    }
}
