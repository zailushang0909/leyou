package com.leyou.cart.threadlocal;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Data
@Slf4j
public class UserHolder implements HandlerInterceptor {
    private ThreadLocal<UserInfo> tl = new ThreadLocal<>();
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            log.error("解析token失败",e);
            return false;
        }
        UserInfo userinfo = payload.getInfo();
        tl.set(userinfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }
}
