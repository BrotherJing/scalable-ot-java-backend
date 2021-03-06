package com.brotherjing;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.brotherjing.config.WebConfig;

@SpringBootApplication
@Import(WebConfig.class)
@ComponentScan(basePackages = "com.brotherjing")
@EnableDubbo(scanBasePackages = "com.brotherjing")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
