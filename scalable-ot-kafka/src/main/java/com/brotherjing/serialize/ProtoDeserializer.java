package com.brotherjing.serialize;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.serialization.Deserializer;

import com.brotherjing.proto.BaseProto;
import com.google.protobuf.InvalidProtocolBufferException;

@Slf4j
public class ProtoDeserializer implements Deserializer<BaseProto.Command> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public BaseProto.Command deserialize(String s, byte[] bytes) {
        try {
            return BaseProto.Command.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse bytes into {}", BaseProto.Command.class.getSimpleName(), e);
            return null;
        }
    }

    @Override
    public void close() {

    }
}
