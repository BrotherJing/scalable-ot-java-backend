package com.brotherjing.core.service;

import java.util.List;

import com.brotherjing.proto.TextProto;

public interface DocService {
    TextProto.Snapshot create();

    TextProto.Snapshot get(String docId);

    TextProto.Snapshot apply(String docId, List<TextProto.Command> commands);
}
