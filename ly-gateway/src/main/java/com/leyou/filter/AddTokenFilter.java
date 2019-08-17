package com.leyou.filter;

import com.leyou.config.JwtProperties;
import com.leyou.task.PrivilegeTokenHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AddTokenFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER+1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private PrivilegeTokenHolder tokenHolder;

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(jwtProperties.getApp().getHeadName(),tokenHolder.getToken());
        return null;
    }
}
