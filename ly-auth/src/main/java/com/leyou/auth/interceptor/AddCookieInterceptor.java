package com.leyou.auth.interceptor;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.task.PrivilegeTokenHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class AddCookieInterceptor implements RequestInterceptor {
    @Autowired
    private PrivilegeTokenHolder tokenHolder;
    @Autowired
    private JwtProperties prop;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(prop.getApp().getHeadName(),tokenHolder.getToken());
    }
}
