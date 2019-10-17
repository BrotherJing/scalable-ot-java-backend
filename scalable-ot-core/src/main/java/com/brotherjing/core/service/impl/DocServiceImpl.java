package com.brotherjing.core.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brotherjing.core.dao.DocDao;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.service.DocService;
import com.brotherjing.core.util.Converter;
import com.brotherjing.core.util.ShortUUID;
import com.brotherjing.proto.TextProto;

@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private DocDao docDao;

    @Override
    public TextProto.Snapshot create() {
        String docId = ShortUUID.generate();
        SnapshotDto dto = SnapshotDto.builder()
                                     .docId(docId)
                                     .data("")
                                     .version(0)
                                     .build();
        docDao.save(dto);
        return Converter.toSnapshotProto(dto);
    }

    @Override
    public TextProto.Snapshot get(String docId) {
        return docDao.findById(docId).map(Converter::toSnapshotProto).orElse(null);
    }

    @Override
    public TextProto.Snapshot apply(String docId, List<TextProto.Command> commands) {
        SnapshotDto dto = docDao.findById(docId).orElse(null);
        if (dto == null) {
            return null;
        }
        for (TextProto.Command command : commands) {
            applySingle(dto, command);
            dto.setVersion(dto.getVersion() + 1);
        }
        docDao.save(dto);
        return Converter.toSnapshotProto(dto);
    }

    private void applySingle(SnapshotDto dto, TextProto.Command command) {
        List<TextProto.Operation> operations;
        TextProto.Operation op = command.getOp();
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
