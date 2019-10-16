package com.brotherjing.core.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.brotherjing.core.dao.DocDao;
import com.brotherjing.core.dto.SnapshotDto;

@Repository
public class DocDaoImpl implements DocDao {

    private Map<String, SnapshotDto> db;

    public DocDaoImpl() {
        db = new HashMap<>();
    }

    @Override
    public boolean save(SnapshotDto dto) {
        db.put(dto.getDocId(), dto);
        return true;
    }

    @Override
    public SnapshotDto fetch(String docId) {
        return db.get(docId);
    }
}
