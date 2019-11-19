package com.brotherjing.core.executor.impl;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.brotherjing.core.executor.AbstractCommandExecutor;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.JsonProto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

@Slf4j
@Component
public class JsonCommandExecutor extends AbstractCommandExecutor<JsonProto.Operations, JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public BaseProto.DocType getSupportedType() {
        return BaseProto.DocType.JSON;
    }

    @Override
    protected Class<JsonProto.Operations> getOpClass() {
        return JsonProto.Operations.class;
    }

    @Override
    protected JsonNode deserialize(String data) throws IOException {
        return OBJECT_MAPPER.readTree(data);
    }

    @Override
    protected String serialize(JsonNode data) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(data);
    }

    @Override
    protected JsonNode applyOp(JsonNode data, JsonProto.Operations op) {
        JsonNode pointer;
        for (JsonProto.Operation operation : op.getOpsList()) {
            pointer = data;
            List<JsonProto.Path> pathList = operation.getPathList();
            for (JsonProto.Path path : pathList.subList(0, pathList.size() - 1)) {
                if (path.getTypeCase().equals(JsonProto.Path.TypeCase.INDEX)) {
                    pointer = pointer.get(path.getIndex());
                } else {
                    pointer = pointer.get(path.getKey());
                }
            }
            // operate on the leaf node
            JsonProto.Path lastPath = pathList.get(pathList.size() - 1);
            if (lastPath.getTypeCase().equals(JsonProto.Path.TypeCase.INDEX)) {
                ArrayNode arrayNode = (ArrayNode)pointer;
                if (operation.hasLi() && operation.hasLd()) {
                    // replace
                    arrayNode.set(lastPath.getIndex(), toJsonNode(operation.getLi()));
                } else if (operation.hasLi()) {
                    // insert
                    arrayNode.insert(lastPath.getIndex(), toJsonNode(operation.getLi()));
                } else if (operation.hasLd()) {
                    // remove
                    arrayNode.remove(lastPath.getIndex());
                }
            } else if (lastPath.getTypeCase().equals(JsonProto.Path.TypeCase.KEY)) {
                ObjectNode objectNode = (ObjectNode)pointer;
                if (operation.hasOi()) {
                    objectNode.set(lastPath.getKey(), toJsonNode(operation.getOi()));
                } else if (operation.hasOd()) {
                    objectNode.remove(lastPath.getKey());
                }
            }
        }
        return data;
    }

    private JsonNode toJsonNode(JsonProto.Payload payload) {
        switch (payload.getTypeCase()) {
        case JSON:
            try {
                return OBJECT_MAPPER.readTree(payload.getJson());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        case TEXT:
            return new TextNode(payload.getText());
        case NUMBER:
            return new IntNode(payload.getNumber());
        }
        return null;
    }
}
