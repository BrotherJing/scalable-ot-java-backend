package com.brotherjing;

public interface Const {
    String TOPIC_REVISION = "revision";
    String TOPIC_OP = "op";

    String REVISION_CONSUMER_GROUP_ID = "revision_consumer";

    String BROADCAST_REGISTRY_PATH = "/ot-broadcast";

    int TAKE_SNAPSHOT_INTERVAL = 50;
}
