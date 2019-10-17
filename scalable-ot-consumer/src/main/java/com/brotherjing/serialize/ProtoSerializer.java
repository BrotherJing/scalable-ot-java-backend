package com.brotherjing.serialize;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.brotherjing.proto.TextProto;

public class ProtoSerializer implements Serializer<TextProto.Command> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, TextProto.Command command) {
        return command.toByteArray();
    }

    @Override
    public void close() {

    }
}
