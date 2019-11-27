package com.brotherjing.broadcast.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brotherjing.config.ZookeeperConfig;
import com.brotherjing.core.dao.RedisDao;
import com.brotherjing.core.loadbalance.ServerEntity;
import com.brotherjing.core.zookeeper.ZookeeperRegistry;

@Service
public class RegistryService {

    @Value(value = "${server.port}")
    private int serverPort;

    @Value(value = "${dubbo.protocol.port}")
    private int dubboPort;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private ZookeeperRegistry registry;

    @PostConstruct
    public void init() {
        registry = new ZookeeperRegistry();
        registry.init(zookeeperConfig);
        register();
    }

    /**
     * Register this node in discovery service
     */
    private void register() {
        boolean success = registry.register(getServerEntity());
        if (!success) {
            throw new RuntimeException("Failed to register");
        }
    }

    /**
     * Register a specific route from docId to node.
     */
    public void registerRoute(String docId) {
        redisDao.addRoute(docId, getServerEntity());
    }

    private ServerEntity getServerEntity() {
        return ServerEntity.builder()
                           .host(getAddress())
                           .serverPort(serverPort)
                           .dubboPort(dubboPort)
                           .build();
    }

    private String getAddress() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get local address", e);
        }
        return inetAddress.getHostAddress();
    }
}
