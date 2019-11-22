package com.brotherjing.broadcast.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get local address", e);
        }
        String address = inetAddress.getHostAddress();
        boolean success = registry.register(Const.BROADCAST_REGISTRY_PATH, address + serverPort);
        if (!success) {
            throw new RuntimeException("Failed to register");
        }
    }
}
