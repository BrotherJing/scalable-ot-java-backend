package com.brotherjing.consumer;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.brotherjing.Const;
import com.brotherjing.core.model.exception.CommandException;
import com.brotherjing.core.service.DocService;
import com.brotherjing.proto.BaseProto.Command;


/**
 * Consume the conflict-free revision stream produced by OT module.
 * This will apply those revisions on the document in sequence and
 * store the document in db.
 */
@Slf4j
@Service
public class RevisionConsumer {

    @Autowired
    private DocService docService;

    @KafkaListener(topics = { Const.TOPIC_REVISION }, groupId = Const.REVISION_CONSUMER_GROUP_ID)
    public void consume(@Payload Command command,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment acknowledgment) {
        log.info("received {} from partition {}, offset = {}", command, partition, offset);
        try {
            docService.apply(command.getDocId(), Collections.singletonList(command));
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }

        // manually send ack after execution to avoid lost message.
        acknowledgment.acknowledge();
    }
}
