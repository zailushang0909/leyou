package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        authService.login(username, password, response);
        return ResponseEntity.ok().build();
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token) {

        return ResponseEntity.ok(authService.verify(token));
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletResponse response,@CookieValue("LY_TOKEN")String token) {
        authService.logout(response,token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("generateTokenByUserInfo")
    public ResponseEntity<String> generateTokenByUserInfo(@RequestBody UserInfo userInfo) {
        return  ResponseEntity.ok(authService.generateTokenByUserInfo(userInfo));
    }
    @GetMapping("generateTokenByAppIdAndSecret")
    public ResponseEntity<String> generateTokenByAppIdAndSecret(
            @RequestParam("id") Long id,
            @RequestParam("secret") String secret) {
        return  ResponseEntity.ok(authService.generateTokenByAppIdAndSecret(id,secret));
    }
}
