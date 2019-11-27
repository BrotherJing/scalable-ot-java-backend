package com.brotherjing.core.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.brotherjing.Const;
import com.brotherjing.core.loadbalance.ServerEntity;

@Repository
public class RedisDao {

    @Autowired
    private RedisTemplate<String, ServerEntity> routeTemplate;

    @Autowired
    private ValueOperations<String, ServerEntity> routeValueOperations;

    public void addRoute(String docId, ServerEntity server) {
        routeValueOperations.set(getRouteKey(docId), server, Const.ROUTE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public ServerEntity getRoute(String docId) {
        return routeValueOperations.get(docId);
    }

    public void removeRoute(String docId) {
        routeTemplate.delete(getRouteKey(docId));
    }

    private String getRouteKey(String docId) {
        return Const.ROUTE_PREFIX + docId;
    }
}
