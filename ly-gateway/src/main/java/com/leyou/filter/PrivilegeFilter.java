package com.leyou.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.config.FilterProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@Slf4j
@EnableConfigurationProperties(FilterProperties.class)
public class PrivilegeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    @Autowired
    private FilterProperties prop;
    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        String requestURI = currentContext.getRequest().getRequestURI();
        List<String> allowPaths = prop.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (requestURI.startsWith(allowPath)) {
                return false;
            }
        }
        return true;
    }

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = null;
        HttpServletRequest request = null;
        try {
            ctx  = RequestContext.getCurrentContext();
            request = ctx.getRequest();
            String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
            UserInfo userInfo = payload.getInfo();
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(),userInfo.getRole(),request.getRequestURI());
        } catch (Exception e) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
            log.error("非法访问，未登录，地址：{}",request.getRemoteHost(),e);
        }
        return null;
    }
}
