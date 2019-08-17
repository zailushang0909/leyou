package com.leyou.auth.service;

import com.leyou.common.auth.entity.UserInfo;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    void login(String username, String password, HttpServletResponse response);

    UserInfo verify(String token);

    void logout(HttpServletResponse response, String token);

    String generateTokenByUserInfo(UserInfo userInfo);

    String generateTokenByAppIdAndSecret(Long id, String secret);
}
