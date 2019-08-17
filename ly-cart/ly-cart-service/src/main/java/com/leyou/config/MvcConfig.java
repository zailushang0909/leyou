package com.leyou.config;

import com.leyou.cart.threadlocal.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserHolder userHolder;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userHolder).addPathPatterns("/**");
    }
}
