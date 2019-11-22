package com.brotherjing.core.util;

import com.brotherjing.core.dto.CommandDto;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.proto.BaseProto;
import com.google.protobuf.InvalidProtocolBufferException;

public class Converter {

    public static BaseProto.Snapshot toSnapshotProto(SnapshotDto dto) {
        return BaseProto.Snapshot.newBuilder()
                                 .setDocId(dto.getDocId())
                                 .setVersion(dto.getVersion())
                                 .setData(dto.getData())
                                 .build();
    }

    public static BaseProto.Command toCommandProto(CommandDto dto) {
        try {
            return BaseProto.Command.parseFrom(dto.getPayload());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BaseProto.Command parseSafe(byte[] payload) {
        try {
            return BaseProto.Command.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }
}
