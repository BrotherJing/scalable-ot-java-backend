package com.brotherjing.api;

import java.util.List;

public interface Broadcast {

    void sendToAll(List<byte[]> commands, boolean excludeSelf);

    void sendTo(String sid, List<byte[]> commands);
}
