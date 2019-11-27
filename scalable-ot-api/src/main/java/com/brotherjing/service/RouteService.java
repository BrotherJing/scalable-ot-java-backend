package com.brotherjing.service;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brotherjing.core.dao.RedisDao;
import com.brotherjing.core.loadbalance.LoadBalancer;
import com.brotherjing.core.loadbalance.ServerEntity;

@Slf4j
@Service
public class RouteService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private LoadBalancer loadBalancer;

    @Autowired
    private RedisDao redisDao;

    public ServerEntity getRoute(String docId) {
        Set<ServerEntity> allServers = discoveryService.getAllServers();

        // first check cached route
        ServerEntity cachedRoute = redisDao.getRoute(docId);
        if (cachedRoute != null) {
            if (allServers.contains(cachedRoute)) {
                // the cached route might differ from the one selected by load balance due to adding new node.
                // but the old node may still have clients connected to it,
                // so use the cached route as long as the node is up.
                return cachedRoute;
            } else {
                // this can happen when node is down and the cache has not expired
                log.warn("Found obsolete route in cache: {}, evicted.", cachedRoute);
                redisDao.removeRoute(docId);
            }
        }

        // if no cache, use load balance to select route
        return loadBalancer.select(allServers, docId);
    }
}
