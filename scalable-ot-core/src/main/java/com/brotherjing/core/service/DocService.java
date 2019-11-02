package com.brotherjing.core.service;

import java.util.List;

import com.brotherjing.proto.BaseProto;

public interface DocService {
    BaseProto.Snapshot create();

    BaseProto.Snapshot get(String docId);

    BaseProto.Snapshot apply(String docId, List<BaseProto.Command> commands);

    List<BaseProto.Command> getOpsSince(String docId, int version);

    List<BaseProto.Command> getOpsBetween(String docId, int from, int to);

    BaseProto.Snapshot getSnapshotAt(String docId, int version);
}
