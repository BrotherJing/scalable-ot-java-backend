package com.brotherjing.broadcast;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.brotherjing.broadcast.config.WebSocketConfig;

@SpringBootApplication
@Import(WebSocketConfig.class)
@ComponentScan(basePackages = "com.brotherjing")
@EnableDubbo(scanBasePackages = "com.brotherjing.broadcast")
public class BroadcastApp {
    public static void main(String[] args) {
        SpringApplication.run(BroadcastApp.class);
    }
}
