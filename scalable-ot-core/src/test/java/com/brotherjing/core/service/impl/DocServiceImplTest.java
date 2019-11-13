package com.brotherjing.core.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.brotherjing.Const;
import com.brotherjing.config.MongoConfig;
import com.brotherjing.core.dto.SnapshotDto;
import com.brotherjing.core.executor.CommandExecutorRegistry;
import com.brotherjing.core.executor.impl.TextCommandExecutor;
import com.brotherjing.proto.BaseProto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
@Import({ DocServiceImpl.class, CommandExecutorRegistry.class, TextCommandExecutor.class })
public class DocServiceImplTest {

    @Autowired
    private DocServiceImpl docService;

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
    public void testGetInitialData() {
        String data = docService.getInitialData(BaseProto.DocType.JSON);
        Assert.assertNotNull(data);
    }

    @Test
    public void testTakeSnapshot() {
        BaseProto.Snapshot doc = docService.create(BaseProto.DocType.PLAIN_TEXT);
        String docId = doc.getDocId();
        int toVersion = Const.TAKE_SNAPSHOT_INTERVAL + 1;
        List<BaseProto.Command> commands = IntStream.range(0, toVersion)
                                                    .mapToObj(i -> BaseProto.Command.newBuilder()
                                                                                    .setVersion(i)
                                                                                    .build())
                                                    .collect(Collectors.toList());
        docService.apply(docId, commands);
        SnapshotDto snapshot = docService.getNearestSnapshot(docId, toVersion);
        Assert.assertNotNull(snapshot);
        Assert.assertEquals(snapshot.getVersion(), toVersion);
    }
}
