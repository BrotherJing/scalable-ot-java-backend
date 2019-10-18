package com.brotherjing.core.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.brotherjing.core.dto.CommandDto;

@Repository
public interface CommandDao extends MongoRepository<CommandDto, String> {
    @Query("{'docId': ?0, 'version': {$gt: ?1}}")
    List<CommandDto> getOpsSince(String docId, int version);
}
