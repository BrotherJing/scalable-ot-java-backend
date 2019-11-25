package com.brotherjing.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brotherjing.Const;
import com.brotherjing.config.ZookeeperConfig;
import com.brotherjing.core.loadbalance.ServerEntity;
import com.brotherjing.core.zookeeper.ZookeeperRegistry;

@Slf4j
@Service
public class DiscoveryService {

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private Set<ServerEntity> cache = ConcurrentHashMap.newKeySet();

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

    public List<ServerEntity> getAllServers() {
        return new ArrayList<>(cache);
    }

    private void addServer(PathChildrenCacheEvent event) {
        ServerEntity entity = toServerEntity(event.getData());
        cache.add(entity);
        log.info("New server: {}", entity);
    }

    private void removeServer(PathChildrenCacheEvent event) {
        ServerEntity entity = toServerEntity(event.getData());
        cache.remove(entity);
        log.info("Remove server: {}", entity);
    }

    private ServerEntity toServerEntity(ChildData data) {
        String path = data.getPath();
        InetSocketAddress dubboAddress = toSocketAddress(path);
        InetSocketAddress serverAddress = toSocketAddress(new String(data.getData()));
        return ServerEntity.builder()
                           .host(dubboAddress.getHostString())
                           .dubboPort(dubboAddress.getPort())
                           .serverPort(serverAddress.getPort())
                           .build();
    }

    private InetSocketAddress toSocketAddress(String path) {
        String address = path.substring(path.lastIndexOf('/') + 1);
        String[] hostAndPort = address.split(":");
        return new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
    }
}
