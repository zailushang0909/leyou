package com.leyou.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("ly.encoder.bcrpt")
public class BcrptProperties {
    private String secret;
    private int strength;
}
