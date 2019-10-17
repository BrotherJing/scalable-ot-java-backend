package com.brotherjing.core.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brotherjing.core.dto.SnapshotDto;

@Repository
public interface DocDao extends MongoRepository<SnapshotDto, String> {
}
