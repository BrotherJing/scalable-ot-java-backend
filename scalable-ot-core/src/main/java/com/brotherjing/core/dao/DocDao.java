package com.brotherjing.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.brotherjing.core.dto.SnapshotDto;

@Repository
public interface DocDao extends MongoRepository<SnapshotDto, String> {


    @Query(value = "{'docId': ?0, 'version': {$lte: ?1}}", sort = "{'version': -1}")
    List<SnapshotDto> getNearestSnapshot(String docId, int version, Pageable pageable);
}
