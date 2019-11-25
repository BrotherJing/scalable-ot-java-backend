package com.brotherjing.broadcast.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brotherjing.Const;
import com.brotherjing.config.ZookeeperConfig;
import com.brotherjing.core.zookeeper.ZookeeperRegistry;

@Service
public class RegistryService {

    @Value(value = "${server.port}")
    private String serverPort;

    @Value(value = "${dubbo.protocol.port}")
    private String dubboPort;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private ZookeeperRegistry registry;

    @PostConstruct
    public void init() {
        registry = new ZookeeperRegistry();
        registry.init(zookeeperConfig);
        register();
    }

    private void register() {
        boolean success = registry.register(Const.BROADCAST_REGISTRY_PATH, serverPort, dubboPort);
        if (!success) {
            throw new RuntimeException("Failed to register");
        }
    }
}
