package com.brotherjing.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperConfig {
    private String connectString;
    private String namespace;
    private int connectionTimeoutMs;
    private int sessionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;
}
