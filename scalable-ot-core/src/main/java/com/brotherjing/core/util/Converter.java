package com.brotherjing.core.util;

import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.proto.TextProto;

public class Converter {

    public static TextProto.Snapshot toSnapshotProto(SnapshotDto dto) {
        return TextProto.Snapshot.newBuilder()
                                 .setDocId(dto.getDocId())
                                 .setVersion(dto.getVersion())
                                 .setData(dto.getData())
                                 .build();
    }
}
