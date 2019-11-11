package com.brotherjing.core.executor.impl;

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.executor.ICommandExecutor;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.TextProto;
import com.google.protobuf.InvalidProtocolBufferException;

@Slf4j
@Component
public class TextCommandExecutor implements ICommandExecutor {

    @Override
    public BaseProto.DocType getSupportedType() {
        return BaseProto.DocType.PLAIN_TEXT;
    }

    @Override
    public void applySingle(SnapshotDto dto, BaseProto.Command command) {
        log.info("command type is {}", command.getOp().getTypeUrl());
        try {
            if (command.getOp().is(TextProto.Operation.class)) {
                TextProto.Operation op = command.getOp().unpack(TextProto.Operation.class);
                applyTextOp(dto, op);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void applyTextOp(SnapshotDto dto, TextProto.Operation op) {
        List<TextProto.Operation> operations;
        if (op.hasMultiple()) {
            operations = op.getMultiple().getOpsList();
        } else {
            operations = Collections.singletonList(op);
        }
        String data = dto.getData();
        int index = 0;
        for (TextProto.Operation operation : operations) {
            switch (operation.getType()) {
            case RETAIN:
                index += operation.getRetain();
                break;
            case INSERT:
                data = data.substring(0, index)
                           .concat(operation.getInsert())
                           .concat(data.substring(index));
                index += operation.getInsert().length();
                break;
            case DELETE:
                int right = Math.min(data.length(), index + operation.getDelete().getDelete());
                data = data.substring(0, index).concat(data.substring(right));
                break;
            default:
                break;
            }
        }
        dto.setData(data);
    }
}
