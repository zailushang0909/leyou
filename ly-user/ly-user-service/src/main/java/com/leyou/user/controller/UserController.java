package com.leyou.user.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import com.leyou.user.pojo.UserDTO;
import com.leyou.user.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("验证用户名或者手机号")
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "校验成功 true表示可以使用，false表示不可以使用"),
                    @ApiResponse(code=400, message = "传入参数不正确")
            }
    )
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUsernameOrPhone(
           @ApiParam("要校验数据：用户名或者手机号")    @PathVariable(name="data") String data,
           @ApiParam("data数据类型：1表示用户名；2表示手机号")    @PathVariable(name="type", required = false) Integer type) {
        return ResponseEntity.ok(userService.checkUsernameOrPhone(data,type));
    }

    @ApiOperation(value="生成验证码")
    @ApiResponses({
            @ApiResponse(code=204,message = "请求已接收"),
            @ApiResponse(code=400,message = "参数不正确")
    })
    @PostMapping("/code")
    public ResponseEntity<Void> GenerateVerificationCode(
           @ApiParam("被发送验证码的手机号") @RequestParam("phone")String phone) {
        userService.GenerateVerificationCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value="注册用户")
    @ApiResponses({
            @ApiResponse(code=201,message = "注册成功"),
            @ApiResponse(code=400,message = "请求参数有误")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(
           @Valid User user,
            BindingResult result,
          @ApiParam("封装页面提交验证码")  @RequestParam("code")String code) {
        if (result.hasErrors()) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value="根据用户名和密码获取用户信息")
    @ApiResponses({
            @ApiResponse(code=200,message = "获取用户信息成功"),
            @ApiResponse(code=500,message = "用户名或者密码不正确")
    })
    @GetMapping("query")
    public ResponseEntity<UserDTO> queryUserByUsernameAndPassword(
          @ApiParam("登陆用户名") @RequestParam("username")  String username,
          @ApiParam("登陆密码") @RequestParam("password") String password
    ) {
        return ResponseEntity.ok(userService.queryUserByUsernameAndPassword(username,password));
    }

}
