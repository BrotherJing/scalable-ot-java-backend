package com.brotherjing.core.service;

import java.util.List;

import com.brotherjing.proto.TextProto;

public interface DocService {
    TextProto.Snapshot create();

    TextProto.Snapshot get(String docId);

    TextProto.Snapshot apply(String docId, List<TextProto.Command> commands);

    List<TextProto.Command> getOpsSince(String docId, int version);

    List<TextProto.Command> getOpsBetween(String docId, int from, int to);

    TextProto.Snapshot getSnapshotAt(String docId, int version);
}
