package com.irum.come2us.global.infrastructure.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class, RedisProperties.class, FileProperties.class})
@Configuration
public class PropertiesConfig {}
