package com.brotherjing.core.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.brotherjing.core.dto.CommandDto;

@Repository
public interface CommandDao extends MongoRepository<CommandDto, String> {
    @Query("{'docId': ?0, 'version': {$gte: ?1}}")
    List<CommandDto> getOpsSince(String docId, int version);

    /**
     * @param docId doc id
     * @param from  inclusive
     * @param to    exclusive
     * @return operations between from and to
     */
    @Query("{'docId': ?0, 'version': {$gte: ?1, $lt: ?2}}")
    List<CommandDto> getOpsBetween(String docId, int from, int to);
}
