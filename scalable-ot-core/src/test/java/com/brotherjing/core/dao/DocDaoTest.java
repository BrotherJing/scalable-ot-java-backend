package com.brotherjing.core.dao;

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
import com.brotherjing.core.dto.SnapshotDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class DocDaoTest {
    @Autowired
    private DocDao docDao;

    @Autowired
    private MongoOperations mongoOps;

    @Before
    public void testSetup() {
        if (!mongoOps.collectionExists(SnapshotDto.class)) {
            mongoOps.createCollection(SnapshotDto.class);
        }
    }

    @After
    public void tearDown() {
        mongoOps.dropCollection(SnapshotDto.class);
    }

    @Test
    public void loadAfterStore() {
        final SnapshotDto dto = SnapshotDto.builder()
                                           .version(1)
                                           .data("data")
                                           .build();
        docDao.save(dto);

        SnapshotDto res = mongoOps.findOne(Query.query(Criteria.where("version").is(1)), SnapshotDto.class);
        System.out.println(res.getDocId());
        Assert.assertEquals(res.getData(), "data");
    }

}
