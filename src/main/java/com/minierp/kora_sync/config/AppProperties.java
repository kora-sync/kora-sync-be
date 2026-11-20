package com.minierp.kora_sync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(@DefaultValue("America/Sao_Paulo") String timezone) {
}