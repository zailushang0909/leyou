package com.leyou.common.auth.utils;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class JwtUtilsTest {

    @Test
    public void generateTokenExpireInMinutes() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(123L);
        userInfo.setRole("狗");
        userInfo.setUsername("张天赐");
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, RsaUtils.getPrivateKey("E:/rsa/id_rsa"), 30);
        System.out.println(token);

    }


    @Test
    public void getInfoFromToken() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(123L);
        userInfo.setRole("狗");
        userInfo.setUsername("张天赐");
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, RsaUtils.getPrivateKey("E:/rsa/id_rsa"), 30);
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, RsaUtils.getPublicKey("E:/rsa/id_rsa.pub"), UserInfo.class);
        System.out.println(payload.getId());
        Date date = payload.getExpiration();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("payload.getExpiration() = " +sdf.format(date));
        UserInfo info = payload.getInfo();
        System.out.println("info = " + info);

    }

}