package com.brotherjing.core.loadbalance.impl;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.brotherjing.core.loadbalance.LoadBalancer;
import com.brotherjing.core.loadbalance.ServerEntity;

/**
 * {@link org.apache.dubbo.rpc.cluster.loadbalance.ConsistentHashLoadBalance}
 */
@Service
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * Cache consistent hash selector based on the given server addresses,
     * since they may change due to adding/removing server.
     */
    private final ConcurrentHashMap<Integer, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public ServerEntity select(Collection<ServerEntity> servers, String key) {
        int selectorKey = System.identityHashCode(servers);
        ConsistentHashSelector selector = selectors.get(selectorKey);
        if (selector == null) {
            selectors.put(selectorKey, new ConsistentHashSelector(servers));
            selector = selectors.get(selectorKey);
        }
        return selector.select(key);
    }

    private static final class ConsistentHashSelector {
        private static final int NUM_VIRTUAL_NODE = 160;
        private final TreeMap<Integer, ServerEntity> virtualNodes;

        /**
         * Construct the ring of virtual nodes.
         */
        ConsistentHashSelector(Collection<ServerEntity> addresses) {
            this.virtualNodes = new TreeMap<>();
            for (ServerEntity address : addresses) {
                String addressStr = address.getServerAddress();
                for (int i = 0; i < NUM_VIRTUAL_NODE; i++) {
                    virtualNodes.put(hash(addressStr + i), address);
                }
            }
        }

        /**
         * Select from the ring of virtual nodes by the hash of key.
         */
        ServerEntity select(String key) {
            int hash = hash(key);
            Map.Entry<Integer, ServerEntity> entry = virtualNodes.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualNodes.firstEntry();
            }
            return entry.getValue();
        }

        private int hash(String key) {
            byte[] md5 = DigestUtils.md5Digest(key.getBytes());
            return ((md5[3] & 0xff) << 24) |
                    ((md5[2] & 0xff) << 16) |
                    ((md5[1] & 0xff) << 8) |
                    (md5[0] & 0xff);
        }

    }
}
