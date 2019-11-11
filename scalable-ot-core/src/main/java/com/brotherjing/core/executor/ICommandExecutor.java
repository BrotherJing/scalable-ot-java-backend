package com.brotherjing.core.executor;

import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.proto.BaseProto;

public interface ICommandExecutor {

    BaseProto.DocType getSupportedType();

    void applySingle(SnapshotDto dto, BaseProto.Command command);

}
