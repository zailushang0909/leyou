package com.leyou.user.interceptor;

import com.leyou.common.auth.entity.ApplicationInfo;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.user.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class PrivilegeInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties prop;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader(prop.getApp().getHeadName());
            Payload<ApplicationInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), ApplicationInfo.class);
            ApplicationInfo appInfo = payload.getInfo();
            List<Long> targetIdList = appInfo.getTargetIdList();
            if (!CollectionUtils.isEmpty(targetIdList)&&targetIdList.contains(prop.getApp().getId())) {
                log.info("微服务：{}成功访问用户微服务", appInfo.getServiceName());
                return true;
            }
        } catch (Exception e) {
        }
        log.error("主机：{},token解析失败拒绝访问", request.getRemoteHost());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
    }

}