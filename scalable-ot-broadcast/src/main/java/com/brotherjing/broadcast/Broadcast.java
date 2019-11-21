package com.brotherjing.broadcast;

import java.util.List;

import com.brotherjing.proto.BaseProto;

public interface Broadcast {

    void sendToAll(List<BaseProto.Command> commands, boolean excludeSelf);

    void sendTo(String sid, List<BaseProto.Command> commands);
}
