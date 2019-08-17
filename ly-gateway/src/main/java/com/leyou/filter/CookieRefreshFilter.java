package com.leyou.filter;

import com.leyou.client.AuthClient;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class CookieRefreshFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Autowired
    private JwtProperties prop;
    @Autowired
    private AuthClient authClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override

    public Object run() throws ZuulException {
        try {
            //获取Cookie Token
            RequestContext context = RequestContext.getCurrentContext();
            HttpServletRequest request = context.getRequest();
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            if (token==null) {
                return null;
            }
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
            if (redisTemplate.hasKey(payload.getId())) {
                return null;
            }
            Date expiration = payload.getExpiration();
            DateTime shouldRefreshToken = new DateTime(expiration).minusMinutes(29);
            //如果有并且成功解析并且当前时间在应该刷新token之后 则重新生成token（Cookie生成时间一分钟后重新生成）
            if (shouldRefreshToken.isBeforeNow()) {
                token = authClient.generateTokenByUserInfo(payload.getInfo());
                Cookie newCookie = new Cookie(prop.getUser().getCookieName(), token);
                newCookie.setMaxAge(prop.getUser().getExpire()*60);
                newCookie.setPath("/");
                newCookie.setHttpOnly(true);
                newCookie.setDomain("leyou.com");
                context.getResponse().addCookie(newCookie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
