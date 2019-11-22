package com.brotherjing.core.zookeeper;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.brotherjing.config.ZookeeperConfig;

@Slf4j
public class ZookeeperRegistry {

    private CountDownLatch latch = new CountDownLatch(1);

    private CuratorFramework curator;

    public void init(ZookeeperConfig config) {
        curator = CuratorFrameworkFactory
                .builder()
                .connectString(config.getConnectString())
                .namespace(config.getNamespace())
                .connectionTimeoutMs(config.getConnectionTimeoutMs())
                .sessionTimeoutMs(config.getSessionTimeoutMs())
                .retryPolicy(new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries()))
                .build();
        curator.start();

        curator.getConnectionStateListenable().addListener((client, newState) -> {
            switch (newState) {
            case CONNECTED:
                latch.countDown();
                break;
            case RECONNECTED:
                break;
            default:
                log.error("Zookeeper failed to connect");
                break;
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean discover(String path, BiConsumer<CuratorFramework, PathChildrenCacheEvent> listener) {
        try {
            Stat stat = curator.checkExists().forPath(path);
            if (stat != null) {
                PathChildrenCache watcher = new PathChildrenCache(curator, path, true);
                watcher.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                watcher.getListenable().addListener(listener::accept);
            } else {
                log.error("Path does not exist: {}", path);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to discover path {}, {}", path, e);
            return false;
        }
        return true;
    }

    public boolean register(String root, String ipAddress) {
        String path = root + "/" + ipAddress;
        try {
            curator.create()
                   .creatingParentsIfNeeded()
                   .withMode(CreateMode.EPHEMERAL)
                   .forPath(path);
            log.info("Registered path in zookeeper: {}", path);
        } catch (Exception e) {
            log.error("Failed to register path {}, {}", path, e);
            return false;
        }
        return true;
    }
}
