package com.leyou.user.service;

import com.leyou.user.pojo.User;
import com.leyou.user.pojo.UserDTO;
import org.apache.commons.lang3.StringUtils;

public interface UserService {

    Boolean checkUsernameOrPhone(String data, Integer type);

    void GenerateVerificationCode(String phone);

    void register(User user, String code);

    UserDTO queryUserByUsernameAndPassword(String username, String password);
}
