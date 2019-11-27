package com.brotherjing;

public interface Const {
    /**
     * Kafka
     */
    String TOPIC_REVISION = "revision";
    String TOPIC_OP = "op";

    String REVISION_CONSUMER_GROUP_ID = "revision_consumer";

    /**
     * Redis
     */
    String ROUTE_PREFIX = "route:";
    int ROUTE_EXPIRE_MINUTES = 30;

    /**
     * Zookeeper
     */
    String BROADCAST_REGISTRY_PATH = "/ot-broadcast";

    /**
     * Dubbo
     */
    String BROADCAST_ADDR_CONTEXT = "broadcast-addr";

    int TAKE_SNAPSHOT_INTERVAL = 50;
}
