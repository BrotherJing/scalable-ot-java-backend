package com.brotherjing.core.executor.impl;

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.brotherjing.core.executor.AbstractCommandExecutor;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.TextProto;

@Slf4j
@Component
public class TextCommandExecutor extends AbstractCommandExecutor<TextProto.Operation, String> {

    @Override
    public BaseProto.DocType getSupportedType() {
        return BaseProto.DocType.PLAIN_TEXT;
    }

    @Override
    protected Class<TextProto.Operation> getOpClass() {
        return TextProto.Operation.class;
    }

    @Override
    protected String deserialize(String data) {
        return data;
    }

    @Override
    protected String serialize(String data) {
        return data;
    }

    @Override
    protected String applyOp(String data, TextProto.Operation op) {
        List<TextProto.Operation> operations;
        if (op.hasMultiple()) {
            operations = op.getMultiple().getOpsList();
        } else {
            operations = Collections.singletonList(op);
        }
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
        return data;
    }
}
