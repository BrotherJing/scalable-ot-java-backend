package com.brotherjing.core.dao;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.brotherjing.config.MongoConfig;
import com.brotherjing.core.dto.CommandDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class CommandDaoTest {

    @Autowired
    private CommandDao commandDao;

    @Autowired
    private MongoOperations mongoOps;

    @Before
    public void testSetup() {
        if (!mongoOps.collectionExists(CommandDto.class)) {
            mongoOps.createCollection(CommandDto.class);
        }
    }

    @After
    public void tearDown() {
        mongoOps.dropCollection(CommandDto.class);
    }

    @Test
    public void loadOpsSince() {
        putOps("123", 10);
        List<CommandDto> res = mongoOps.find(Query.query(
                Criteria.where("docId").is("123")
                        .andOperator(Criteria.where("version").gte(6))), CommandDto.class);
        Assert.assertEquals(5, res.size());
    }

    @Test
    public void loadOpsSinceWithQueryAnnotation() {
        // try with query annotation
        putOps("456", 20);
        List<CommandDto> res = commandDao.getOpsSince("456", 11);
        Assert.assertEquals(10, res.size());
    }

    private void putOps(String docId, int count) {
        for (int i = 1; i <= count; i++) {
            final CommandDto dto = CommandDto.builder()
                                             .version(i)
                                             .docId(docId)
                                             .build();
            commandDao.save(dto);
        }
    }
}
