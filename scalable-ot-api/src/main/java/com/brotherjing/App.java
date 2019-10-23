package com.brotherjing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.brotherjing.broadcast.config.WebSocketConfig;
import com.brotherjing.config.WebConfig;

@SpringBootApplication
@Import({ WebConfig.class, WebSocketConfig.class })
@ComponentScan(basePackages = "com.brotherjing")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
