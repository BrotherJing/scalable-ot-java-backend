package com.brotherjing.core.dao;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.brotherjing.config.MongoConfig;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.util.IDUtils;

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
        Assert.assertEquals(res.getData(), "data");
    }

    @Test
    public void testLoadNearestSnapshot() {
        final SnapshotDto dto = SnapshotDto.builder()
                                           .version(1)
                                           .data("data")
                                           .docId("123")
                                           .id(IDUtils.generateSnapshotPK("123", 1))
                                           .build();
        docDao.save(dto);

        // save another snapshot
        dto.setVersion(2);
        dto.setId(IDUtils.generateSnapshotPK("123", 2));
        docDao.save(dto);

        List<SnapshotDto> result = docDao.getNearestSnapshot("123", 3, PageRequest.of(0, 1));
        Assert.assertTrue(result != null && result.size() == 1);
        // should fetch the greatest version less than the queried version.
        Assert.assertEquals(result.get(0).getVersion(), 2);
    }

}
