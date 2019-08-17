package com.leyou.config;

import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@ConfigurationProperties("ly.jwt")
@Component
@Data
@Slf4j
public class JwtProperties implements InitializingBean {

    private String publicFilePath;
    private PublicKey publicKey;
    private String cookieName;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey = RsaUtils.getPublicKey(publicFilePath);
        } catch (Exception e) {
           log.error("获取公钥失败");
            throw new LyException(ExceptionEnum.LOAD_PRIVATEKEY_FAIL);
        }
    }
}
