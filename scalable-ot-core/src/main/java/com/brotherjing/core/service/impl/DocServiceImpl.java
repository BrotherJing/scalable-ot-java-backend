package com.brotherjing.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.brotherjing.Const;
import com.brotherjing.core.dao.CommandDao;
import com.brotherjing.core.dao.DocDao;
import com.brotherjing.core.dto.CommandDto;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.service.DocService;
import com.brotherjing.core.util.Converter;
import com.brotherjing.core.util.IDUtils;
import com.brotherjing.core.util.ShortUUID;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.TextProto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;

@Slf4j
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private DocDao docDao;

    @Autowired
    private CommandDao commandDao;

    @Override
    public BaseProto.Snapshot create() {
        String docId = ShortUUID.generate();
        SnapshotDto dto = SnapshotDto.builder()
                                     .id(IDUtils.generateSnapshotPK(docId))
                                     .docId(docId)
                                     .data("")
                                     .version(0)
                                     .build();
        // also create initial snapshot
        SnapshotDto initSnapshot = SnapshotDto.builder()
                                              .id(IDUtils.generateSnapshotPK(docId, 0))
                                              .docId(docId)
                                              .data("")
                                              .version(0)
                                              .build();
        docDao.saveAll(Lists.newArrayList(dto, initSnapshot));
        return Converter.toSnapshotProto(dto);
    }

    @Override
    public BaseProto.Snapshot get(String docId) {
        return docDao.findById(IDUtils.generateSnapshotPK(docId))
                     .map(Converter::toSnapshotProto).orElse(null);
    }

    @Override
    public List<BaseProto.Command> getOpsSince(String docId, int version) {
        List<CommandDto> ops = commandDao.getOpsSince(docId, version);
        if (ops == null || ops.isEmpty()) {
            return Collections.emptyList();
        }
        return ops.stream().map(Converter::toCommandProto).collect(Collectors.toList());
    }

    @Override
    public List<BaseProto.Command> getOpsBetween(String docId, int from, int to) {
        List<CommandDto> ops = commandDao.getOpsBetween(docId, from, to);
        if (ops == null || ops.isEmpty()) {
            return Collections.emptyList();
        }
        return ops.stream().map(Converter::toCommandProto).collect(Collectors.toList());
    }

    @VisibleForTesting
    protected SnapshotDto getNearestSnapshot(String docId, int version) {
        List<SnapshotDto> snapshots = docDao.getNearestSnapshot(docId, version, PageRequest.of(0, 1));
        if (snapshots == null || snapshots.isEmpty()) {
            return null;
        }
        return snapshots.get(0);
    }

    @Override
    public BaseProto.Snapshot getSnapshotAt(String docId, int version) {
        SnapshotDto snapshot = getNearestSnapshot(docId, version);
        if (snapshot == null) {
            return null;
        }
        if (snapshot.getVersion() == version) {
            // version is equal, return directly
            return Converter.toSnapshotProto(snapshot);
        }
        List<BaseProto.Command> ops = getOpsBetween(docId, snapshot.getVersion(), version);
        apply(snapshot, ops);
        return Converter.toSnapshotProto(snapshot);
    }

    @Override
    public BaseProto.Snapshot apply(String docId, List<BaseProto.Command> commands) {
        SnapshotDto dto = docDao.findById(IDUtils.generateSnapshotPK(docId)).orElse(null);
        if (dto == null) {
            return null;
        }
        int versionBeforeApply = dto.getVersion();

        apply(dto, commands);
        docDao.save(dto);

        tryTakeSnapshot(dto, versionBeforeApply);
        return Converter.toSnapshotProto(dto);
    }

    private void apply(SnapshotDto dto, List<BaseProto.Command> commands) {
        if (commands == null) {
            return;
        }
        for (BaseProto.Command command : commands) {
            applySingle(dto, command);
            dto.setVersion(dto.getVersion() + 1);
        }
    }

    private void applySingle(SnapshotDto dto, BaseProto.Command command) {
        if (BaseProto.DocType.PLAIN_TEXT.equals(command.getType())) {
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

    /**
     * Try take snapshot at certain intervals
     *
     * @param dto        the latest document dto
     * @param oldVersion version to compare
     */
    private void tryTakeSnapshot(SnapshotDto dto, int oldVersion) {
        // if the version pass another checkpoint
        if (dto.getVersion() / Const.TAKE_SNAPSHOT_INTERVAL > oldVersion / Const.TAKE_SNAPSHOT_INTERVAL) {
            dto.setId(IDUtils.generateSnapshotPK(dto.getDocId(), dto.getVersion()));
            docDao.save(dto);
        }
    }
}
