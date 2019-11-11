package com.brotherjing.core.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brotherjing.proto.BaseProto;

@Component
public class CommandExecutorRegistry {

    private List<ICommandExecutor> executorList;

    private Map<BaseProto.DocType, ICommandExecutor> executorMap;

    @Autowired
    public CommandExecutorRegistry(List<ICommandExecutor> executorList) {
        this.executorList = executorList;
        this.executorMap = new HashMap<>();
        for (ICommandExecutor executor : executorList) {
            executorMap.put(executor.getSupportedType(), executor);
        }
    }

    public ICommandExecutor getCommandExecutor(BaseProto.DocType docType) {
        return executorMap.get(docType);
    }

}
