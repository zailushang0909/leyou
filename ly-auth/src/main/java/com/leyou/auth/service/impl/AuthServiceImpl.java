package com.leyou.auth.service.impl;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.Application;
import com.leyou.auth.mapper.ApplicationMapper;
import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.ApplicationInfo;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.user.client.UserClient;
import com.leyou.user.pojo.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void login(String username, String password, HttpServletResponse response) {

        try {
            //跨服务调用user微服务 根据usernameandpassword 查询用户信息
            UserDTO userDTO = userClient.queryUserByUsernameAndPassword(username, password);
            //获取用户信息将userDTO装换成UserInfo
            UserInfo userInfo = BeanHelper.copyProperties(userDTO, UserInfo.class);
            userInfo.setRole("USER_ROOT");
            //调用JWT工具类创建token
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProperties.getPrivateKey(), 30);
            //创建cookie 将token存入cookie
            Cookie cookie = new Cookie(jwtProperties.getUser().getCookieName(),token);
            cookie.setHttpOnly(true);
            cookie.setDomain(jwtProperties.getUser().getCookieDomain());
            cookie.setPath("/");
            cookie.setMaxAge(jwtProperties.getUser().getExpire()*60);
            response.addCookie(cookie);
        } catch (Exception e) {
            log.error("用户名或者密码错误",e);
            throw new LyException(ExceptionEnum.LOGIN_FAIL);
        }
    }

    @Override
    public UserInfo verify(String token) {
        try {
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
            if (!redisTemplate.hasKey(payload.getId())) {
                return payload.getInfo();
            }
        } catch (Exception e) {
            log.error("token无效",e);
        }
        throw new LyException(ExceptionEnum.INVALID_TOKEN);
    }

    @Override
    public void logout(HttpServletResponse response, String token) {
        //解析token 不能解析咋抛出异常提示token无效
        Payload<UserInfo> payload=null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_TOKEN);
        }
        long time = payload.getExpiration().getTime()-System.currentTimeMillis() ;
        Cookie cookie = new Cookie(jwtProperties.getUser().getCookieName(),"");
        cookie.setHttpOnly(true);
        cookie.setDomain(jwtProperties.getUser().getCookieDomain());
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        if (time>3000) {
            redisTemplate.opsForValue().set(payload.getId(),"", time, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String generateTokenByUserInfo(UserInfo userInfo) {
        return JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getUser().getExpire());
    }

    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public String generateTokenByAppIdAndSecret(Long id, String secret) {
        //根据id查询application
        Application app = applicationMapper.selectByPrimaryKey(id);
        //密码验证失败抛错、成功继续
        if (app==null ||!passwordEncoder.matches(secret,app.getSecret())) {
            throw new LyException(ExceptionEnum.LOGIN_FAIL);
        }

        //将application转换成applicationInfo对象
        ApplicationInfo appInfo = BeanHelper.copyProperties(app, ApplicationInfo.class);
        List<Long> ids = applicationMapper.selectTargetIdsById(id);
        appInfo.setTargetIdList(ids);
        //调用工具类生成token返回
        String token = null;
        try {
            token = JwtUtils.generateTokenExpireInMinutes(appInfo, jwtProperties.getPrivateKey(), jwtProperties.getApp().getExpire());
            log.info("服务{}生成token成功", appInfo.getServiceName());
        } catch (Exception e) {
            log.error("服务{}生成token失败", appInfo.getServiceName(), e);
            throw new LyException(ExceptionEnum.COMMON_FAIL);
        }
        return token;
    }
}
