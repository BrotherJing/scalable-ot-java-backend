package com.brotherjing.core.executor;

import java.util.List;

import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.model.exception.CommandException;
import com.brotherjing.proto.BaseProto;

public interface ICommandExecutor {

    BaseProto.DocType getSupportedType();

    void apply(SnapshotDto dto, List<BaseProto.Command> commands) throws CommandException;
}
