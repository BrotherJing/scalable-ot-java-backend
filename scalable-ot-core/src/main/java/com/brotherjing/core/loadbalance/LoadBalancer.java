package com.brotherjing.core.loadbalance;

import java.util.Collection;

public interface LoadBalancer {

    ServerEntity select(Collection<ServerEntity> servers, String key);

}
