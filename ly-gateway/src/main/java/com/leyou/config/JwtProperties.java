package com.leyou.config;

import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PublicKey;

@ConfigurationProperties("ly.jwt")
@Data
@Slf4j
public class JwtProperties implements InitializingBean {
    private String publicFilePath;
    private PublicKey publicKey;
    private UserTokenProperties user = new UserTokenProperties();
    private AppTokenProperties app = new AppTokenProperties();


    @Data
    public class AppTokenProperties{
        private Long id;
        private String secret;
        private String expire;
        private String headName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey = RsaUtils.getPublicKey(publicFilePath);
        } catch (Exception e) {
            log.error("解析公钥失败");
            throw new LyException(ExceptionEnum.LOAD_PRIVATEKEY_FAIL);
        }
    }

    @Data
    public class UserTokenProperties{
        private int expire;
        private String cookieName;
        private String cookieDomain;
    }

}
