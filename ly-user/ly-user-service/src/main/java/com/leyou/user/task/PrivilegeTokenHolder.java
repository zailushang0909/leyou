package com.leyou.user.task;

import com.leyou.user.client.AuthClient;
import com.leyou.user.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class PrivilegeTokenHolder {

    private String token;
    private static final long TOKEN_RETRY_INTERVAL = 10000L;
    private static final long TOKEN_REFRESH_INTERVAL = 24*60*60*1000L;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private JwtProperties prop;
    @Value("${spring.application.name}")
    private String AppName;

    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void loadToken() throws InterruptedException {
        try {
            String token = authClient.generateTokenByAppIdAndSecret(prop.getApp().getId(), prop.getApp().getSecret());
            this.token = token;
            log.info("微服务{}，获取token成功：{}",AppName,token);
        } catch (Exception e) {
            log.error("微服务{}，获取token失败",AppName);
            Thread.sleep(TOKEN_RETRY_INTERVAL);
            loadToken();
        }
    }

}
