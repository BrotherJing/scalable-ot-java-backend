package com.brotherjing.core.util;

import com.brotherjing.core.dto.CommandDto;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.proto.TextProto;
import com.google.protobuf.InvalidProtocolBufferException;

public class Converter {

    public static TextProto.Snapshot toSnapshotProto(SnapshotDto dto) {
        return TextProto.Snapshot.newBuilder()
                                 .setDocId(dto.getDocId())
                                 .setVersion(dto.getVersion())
                                 .setData(dto.getData())
                                 .build();
    }

    public static TextProto.Command toCommandProto(CommandDto dto) {
        try {
            return TextProto.Command.parseFrom(dto.getPayload());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }
}
