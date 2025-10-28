package com.irum.come2us;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class Come2usApplication {

    public static void main(String[] args) {
        SpringApplication.run(Come2usApplication.class, args);
    }
}
