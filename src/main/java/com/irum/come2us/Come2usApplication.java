package com.irum.come2us;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class Come2usApplication {

    public static void main(String[] args) {
        SpringApplication.run(Come2usApplication.class, args);
    }
}
