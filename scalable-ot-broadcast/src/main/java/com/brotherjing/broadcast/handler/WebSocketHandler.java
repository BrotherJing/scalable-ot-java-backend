package com.brotherjing.broadcast.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.brotherjing.proto.BaseProto;

@Slf4j
@Component
public class WebSocketHandler extends BinaryWebSocketHandler {

    private Map<String, Map<String, WebSocketSession>> clientsByDocId = new ConcurrentHashMap<>();

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseProto.Command command = BaseProto.Command.parseFrom(message.getPayload().array());
        if (command.getInit()) {
            register(command.getDocId(), command.getSid(), session);
        } else {
            log.warn("command sent through web socket is ignored: {}", command);
        }
    }

    public void sendToAll(List<BaseProto.Command> commands, boolean excludeSelf) {
        if (CollectionUtils.isEmpty(commands)) {
            return;
        }
        String docId = commands.get(0).getDocId();
        String commandSid = commands.get(0).getSid();
        Map<String, WebSocketSession> clients = clientsByDocId.getOrDefault(docId, Collections.emptyMap());
        List<String> failed = new ArrayList<>();
        for (Map.Entry<String, WebSocketSession> entry : clients.entrySet()) {
            String sid = entry.getKey();
            WebSocketSession client = entry.getValue();
            if (excludeSelf && sid.equals(commandSid)) {
                continue;
            }
            if (!client.isOpen()) {
                failed.add(sid);
                continue;
            }
            try {
                for (BaseProto.Command command : commands) {
                    client.sendMessage(new BinaryMessage(command.toByteArray()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                failed.add(sid);
            }
        }
        for (String sid : failed) {
            clients.remove(sid);
        }
    }

    public void sendToAll(BaseProto.Command command, boolean excludeSelf) {
        sendToAll(Collections.singletonList(command), excludeSelf);
    }

    public void sendTo(String sid, List<BaseProto.Command> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return;
        }
        String docId = commands.get(0).getDocId();
        WebSocketSession client = clientsByDocId.getOrDefault(docId, Collections.emptyMap()).get(sid);
        if (client == null || !client.isOpen()) {
            return;
        }
        try {
            for (BaseProto.Command command : commands) {
                client.sendMessage(new BinaryMessage(command.toByteArray()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register(String docId, String sid, WebSocketSession client) {
        clientsByDocId.putIfAbsent(docId, new ConcurrentHashMap<>());
        clientsByDocId.get(docId).put(sid, client);
    }
}
