package com.leyou.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(GlobalCORSConfigProperties.class)
public class GlobalCORSConfig {

    @Autowired
    private GlobalCORSConfigProperties properties;

    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        properties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        //2) 是否发送Cookie信息
        config.setAllowCredentials(properties.getAllowCredentials());
        //3) 允许的请求方式
        properties.getAllowedMethods().forEach(config::addAllowedMethod);
        // 4）允许的头信息
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(properties.getFilterPath(), config);
        config.setMaxAge(properties.getMaxAge());
        //3.返回新的CORSFilter.
        return new CorsFilter(configSource);
    }

}
