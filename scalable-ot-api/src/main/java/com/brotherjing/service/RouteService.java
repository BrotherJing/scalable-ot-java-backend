package com.brotherjing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brotherjing.core.loadbalance.LoadBalancer;
import com.brotherjing.core.loadbalance.ServerEntity;

@Service
public class RouteService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private LoadBalancer loadBalancer;

    public ServerEntity getRoute(String docId) {
        List<ServerEntity> allServers = discoveryService.getAllServers();
        return loadBalancer.select(allServers, docId);
    }
}
