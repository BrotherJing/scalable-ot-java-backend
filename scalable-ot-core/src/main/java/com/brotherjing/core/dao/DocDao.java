package com.brotherjing.core.dao;

import com.brotherjing.core.dto.SnapshotDto;

public interface DocDao {
    boolean save(SnapshotDto dto);

    SnapshotDto fetch(String docId);
}
