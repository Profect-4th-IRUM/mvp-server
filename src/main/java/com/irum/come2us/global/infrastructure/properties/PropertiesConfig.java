package com.irum.come2us.global.infrastructure.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    JwtProperties.class,
    RedisProperties.class,
    TossProperties.class,
    FileProperties.class
})
public class PropertiesConfig {}
