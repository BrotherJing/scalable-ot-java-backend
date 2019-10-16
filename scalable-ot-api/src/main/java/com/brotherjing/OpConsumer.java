package com.brotherjing;

import java.io.IOException;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.brotherjing.core.service.DocService;
import com.brotherjing.proto.TextProto.Command;


@Slf4j
@Service
public class OpConsumer {

    @Autowired
    private DocService docService;

    @KafkaListener(topicPartitions = @TopicPartition(topic = Const.TOPIC_OP, partitions = { "0", "1", "2" }))
    public void consume(@Payload Command command,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws IOException {
        log.info("received {} from partition {}", command, partition);
        if (command == null) {
            return;
        }
        docService.apply(command.getDocId(), Collections.singletonList(command));
    }
}
