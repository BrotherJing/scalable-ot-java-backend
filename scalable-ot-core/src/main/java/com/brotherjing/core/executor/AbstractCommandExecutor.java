package com.brotherjing.core.executor;

import java.util.List;

import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.model.exception.CommandException;
import com.brotherjing.proto.BaseProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public abstract class AbstractCommandExecutor<T extends Message, D> implements ICommandExecutor {

    @Override
    public void apply(SnapshotDto dto, List<BaseProto.Command> commands) throws CommandException {
        D data;
        try {
            data = deserialize(dto.getData());
        } catch (Exception e) {
            throw new CommandException(e);
        }
        for (BaseProto.Command command : commands) {
            data = applySingle(data, command);
        }
        try {
            dto.setData(serialize(data));
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    private D applySingle(D data, BaseProto.Command command) {
        try {
            if (command.getOp().is(getOpClass())) {
                T op = command.getOp().unpack(getOpClass());
                return applyOp(data, op);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected abstract D deserialize(String data) throws Exception;

    protected abstract String serialize(D data) throws Exception;

    protected abstract Class<T> getOpClass();

    protected abstract D applyOp(D data, T op);
}
