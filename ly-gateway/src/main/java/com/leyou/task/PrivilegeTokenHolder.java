package com.leyou.task;

import com.leyou.client.AuthClient;
import com.leyou.config.JwtProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class PrivilegeTokenHolder {

    @Autowired
    private JwtProperties prop;
    @Autowired
    private AuthClient authClient;

    private String token;
    private static final long TOKEN_RETRY_INTERVAL = 10000L;
    private static final long TOKEN_REFRESH_INTERVAL = 24*60*60*1000L;

    @Scheduled(fixedDelay =TOKEN_REFRESH_INTERVAL)
    public void loadToken() throws InterruptedException {
        try {
            String token = authClient.generateTokenByAppIdAndSecret(prop.getApp().getId(), prop.getApp().getSecret());
            this.token = token;
            log.info("微服务{}，获取token成功,Token:{}",prop.getApp().getSecret(),token);
        } catch (Exception e) {
            log.error("微服务{},获取token失败",prop.getApp().getSecret());
            Thread.sleep(TOKEN_RETRY_INTERVAL);
            loadToken();
        }

    }

}
