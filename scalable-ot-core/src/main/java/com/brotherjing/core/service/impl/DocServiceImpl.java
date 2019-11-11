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
import com.brotherjing.core.executor.CommandExecutorRegistry;
import com.brotherjing.core.executor.ICommandExecutor;
import com.brotherjing.core.service.DocService;
import com.brotherjing.core.util.Converter;
import com.brotherjing.core.util.IDUtils;
import com.brotherjing.core.util.ShortUUID;
import com.brotherjing.proto.BaseProto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

@Slf4j
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private CommandExecutorRegistry registry;

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
        if (commands == null || commands.isEmpty()) {
            return;
        }
        ICommandExecutor executor = registry.getCommandExecutor(commands.get(0).getType());
        if (executor == null) {
            log.error("Cannot find executor for this command type: {}", commands.get(0).getType().name());
            return;
        }
        for (BaseProto.Command command : commands) {
            executor.applySingle(dto, command);
            dto.setVersion(dto.getVersion() + 1);
        }
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
