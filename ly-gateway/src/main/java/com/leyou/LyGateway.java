package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients
@EnableScheduling
public class LyGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class, args);
    }
}
