package com.leyou.auth.task;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class PrivilegeTokenHolder {

    private String token;
    private static final long TOKEN_RETRY_INTERVAL = 10000L;
    private static final long TOKEN_REFRESH_INTERVAL = 24*60*60*1000L;

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void loadToken() throws InterruptedException {
        try {
            String token = authService.generateTokenByAppIdAndSecret(jwtProperties.getApp().getId(), jwtProperties.getApp().getSecret());
            this.token = token;
            log.info("微服务：{}，获取token成功", jwtProperties.getApp().getSecret());
        } catch (Exception e) {
            log.error("微服务：{}，获取token失败", jwtProperties.getApp().getSecret());
            Thread.sleep(TOKEN_RETRY_INTERVAL);
            loadToken();
        }
    }

}
