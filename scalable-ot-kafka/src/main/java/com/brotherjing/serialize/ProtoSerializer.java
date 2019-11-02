package com.brotherjing.serialize;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.brotherjing.proto.BaseProto;

public class ProtoSerializer implements Serializer<BaseProto.Command> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, BaseProto.Command command) {
        return command.toByteArray();
    }

    @Override
    public void close() {

    }
}
