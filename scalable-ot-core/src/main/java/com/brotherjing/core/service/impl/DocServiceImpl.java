package com.brotherjing.core.service.impl;

import java.util.Arrays;
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
import com.brotherjing.core.model.exception.CommandException;
import com.brotherjing.core.service.DocService;
import com.brotherjing.core.util.Converter;
import com.brotherjing.core.util.IDUtils;
import com.brotherjing.core.util.ShortUUID;
import com.brotherjing.proto.BaseProto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public BaseProto.Snapshot create(BaseProto.DocType docType) {
        String docId = ShortUUID.generate();
        SnapshotDto dto = SnapshotDto.builder()
                                     .id(IDUtils.generateSnapshotPK(docId))
                                     .docId(docId)
                                     .data(getInitialData(docType))
                                     .version(0)
                                     .build();
        // also create initial snapshot
        SnapshotDto initSnapshot = dto.clone();
        initSnapshot.setId(IDUtils.generateSnapshotPK(docId, 0));
        docDao.saveAll(Lists.newArrayList(dto, initSnapshot));
        return Converter.toSnapshotProto(dto);
    }

    @VisibleForTesting
    String getInitialData(BaseProto.DocType docType) {
        if (BaseProto.DocType.JSON.equals(docType)) {
            String[][] data = new String[10][10];
            for (String[] row : data) {
                Arrays.fill(row, "");
            }
            try {
                return new ObjectMapper().writeValueAsString(data);
            } catch (JsonProcessingException e) {
                return "{}";
            }
        }
        return "";
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
        try {
            apply(snapshot, ops);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        return Converter.toSnapshotProto(snapshot);
    }

    @Override
    public BaseProto.Snapshot apply(String docId, List<BaseProto.Command> commands) throws CommandException {
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

    private void apply(SnapshotDto dto, List<BaseProto.Command> commands) throws CommandException {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        ICommandExecutor executor = registry.getCommandExecutor(commands.get(0).getType());
        if (executor == null) {
            log.error("Cannot find executor for this command type: {}", commands.get(0).getType().name());
            return;
        }
        int version = dto.getVersion();
        // this achieve idempotent by removing duplicated commands
        commands = commands.stream()
                           .filter(command -> command.getVersion() >= version)
                           .collect(Collectors.toList());
        executor.apply(dto, commands);
        dto.setVersion(version + commands.size());
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
