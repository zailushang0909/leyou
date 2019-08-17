package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

@ConfigurationProperties("ly.jwt")
@Data
@Slf4j
public class JwtProperties implements InitializingBean {
    private String privateFilePath;
    private String publicFilePath;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private UserTokenProperties user=new UserTokenProperties();
    private AppTokenProperties app = new AppTokenProperties();

    @Data
    public class UserTokenProperties{
        private int expire;
        private String cookieName;
        private String cookieDomain;
    }

    @Data
    public class AppTokenProperties{
        private Long id;
        private String secret;
        private int expire;
        private String headName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.privateKey = RsaUtils.getPrivateKey(privateFilePath);
            this.publicKey = RsaUtils.getPublicKey(publicFilePath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！",e);
            throw new LyException(ExceptionEnum.LOAD_PRIVATEKEY_FAIL);
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(BcrptProperties bcrptProperties) {
        SecureRandom secureRandom = new SecureRandom(bcrptProperties.getSecret().getBytes());
        return new BCryptPasswordEncoder(bcrptProperties.getStrength(), secureRandom);
    }
}
