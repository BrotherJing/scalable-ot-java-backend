package com.brotherjing.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;

import com.brotherjing.Const;
import com.brotherjing.config.ZookeeperConfig;
import com.brotherjing.core.zookeeper.ZookeeperRegistry;

@Slf4j
public class DiscoveryService {

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private Set<InetSocketAddress> cache;

    @PostConstruct
    public void init() {
        ZookeeperRegistry registry = new ZookeeperRegistry();
        registry.init(zookeeperConfig);

        registry.discover(Const.BROADCAST_REGISTRY_PATH, (curator, event) -> {
            switch (event.getType()) {
            case CHILD_ADDED:
                addServer(event);
                break;
            case CHILD_REMOVED:
                removeServer(event);
                break;
            default:
                break;
            }
        });
    }

    public List<InetSocketAddress> getAllServers() {
        return new ArrayList<>(cache);
    }

    private void addServer(PathChildrenCacheEvent event) {
        String path = event.getData().getPath();
        cache.add(toSocketAddress(path));
        log.info("New server: {}", path);
    }

    private void removeServer(PathChildrenCacheEvent event) {
        String path = event.getData().getPath();
        cache.remove(toSocketAddress(path));
        log.info("Remove server: {}", path);
    }

    private InetSocketAddress toSocketAddress(String path) {
        String address = path.substring(path.lastIndexOf('/') + 1);
        String[] hostAndPort = address.split(":");
        return new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
    }
}
