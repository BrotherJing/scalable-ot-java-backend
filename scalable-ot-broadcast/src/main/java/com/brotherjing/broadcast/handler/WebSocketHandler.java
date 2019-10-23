package com.brotherjing.broadcast.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.brotherjing.proto.TextProto;

@Slf4j
@Component
public class WebSocketHandler extends BinaryWebSocketHandler {

    private Map<String, List<WebSocketSession>> clientsByDocId = new HashMap<>();

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        TextProto.Command command = TextProto.Command.parseFrom(message.getPayload().array());
        if (command.getInit()) {
            register(command.getDocId(), session);
        } else {
            log.warn("command sent through web socket is ignored: {}", command);
        }
    }

    public void sendToAll(TextProto.Command command) {
        List<WebSocketSession> clients = clientsByDocId.getOrDefault(command.getDocId(), Collections.emptyList());
        for (Iterator<WebSocketSession> it = clients.iterator(); it.hasNext(); ) {
            WebSocketSession client = it.next();
            if (!client.isOpen()) {
                it.remove();
                continue;
            }
            try {
                client.sendMessage(new BinaryMessage(command.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
                it.remove();
            }
        }
    }

    private void register(String docId, WebSocketSession client) {
        clientsByDocId.putIfAbsent(docId, new ArrayList<>());
        clientsByDocId.get(docId).add(client);
    }
}
