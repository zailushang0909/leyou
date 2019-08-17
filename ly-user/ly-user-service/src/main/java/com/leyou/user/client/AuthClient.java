package com.leyou.user.client;


import com.leyou.common.auth.entity.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface AuthClient {

    @PostMapping("generateTokenByUserInfo")
    String generateTokenByUserInfo(UserInfo userInfo);

    @GetMapping("generateTokenByAppIdAndSecret")
    String generateTokenByAppIdAndSecret(
            @RequestParam("id") Long id,
            @RequestParam("secret") String secret);
}
