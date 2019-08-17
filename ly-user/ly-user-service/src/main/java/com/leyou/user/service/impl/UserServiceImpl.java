package com.leyou.user.service.impl;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.pojo.UserDTO;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    private final static String USER_CODE_REDIS_KEY_PREFIX = "user:code:phone:";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Boolean checkUsernameOrPhone(String data, Integer type) {

            //如果data为空或者内容为空则抛出异常400 参数有误
            if (StringUtils.isBlank(data)) {
                throw new LyException(ExceptionEnum.PARAM_ERROR);
            }
            //如果type为空则赋值为1
            type = type == null ? 1 : type;
            User user = new User();
            switch(type){
                    case 1: user.setUsername(data); break;
                    case 2: user.setPhone(data); break;
                    default: throw new LyException(ExceptionEnum.PARAM_ERROR);
                }

            //去数据库中查询满足条件的个数
            int count = userMapper.selectCount(user);
            //如果count==0则返回true
            return count==0;

    }

    @Override
    public void GenerateVerificationCode(String phone) {

            //校验手机号 校验失败则响应参数有误
            if (!RegexUtils.isPhone(phone)) {
                throw new LyException(ExceptionEnum.PARAM_ERROR);
            }
            //调用工具类生成6位验证码
            String code = NumberUtils.generateCode(6);
            //将验证码存入redis
            redisTemplate.opsForValue().set(USER_CODE_REDIS_KEY_PREFIX.concat(phone),code,5, TimeUnit.MINUTES);
            //调用组装数据 发送给消息中间件{phone:"",code,""}
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME, MQConstants.RoutingKey.VERIFY_CODE_KEY,msg);

    }

    @Override
    @Transactional
    public void register(User user, String code) {

            //校验验证码是否正确
            String key = USER_CODE_REDIS_KEY_PREFIX.concat(user.getPhone());
            if (!StringUtils.equals(code,redisTemplate.opsForValue().get(key))) {
                throw new LyException(ExceptionEnum.VERIFY_CODE_FAIL);
            }
            //对密码进行加密后封装进user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            //将user存入数据库
            int count = userMapper.insertSelective(user);
            if (count!=1) {
                throw new LyException(ExceptionEnum.FAIL_INSERT);
            }

            if (redisTemplate.hasKey(key) && redisTemplate.getExpire(key)>5) {
                    redisTemplate.delete(key);
            }

    }

    @Override
    public UserDTO queryUserByUsernameAndPassword(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user = userMapper.selectOne(user);
        if (user==null) {
            throw new LyException(ExceptionEnum.LOGIN_FAIL);
        }
        if (!passwordEncoder.matches(password,user.getPassword())) {
            throw new LyException(ExceptionEnum.LOGIN_FAIL);
        }
        return BeanHelper.copyProperties(user, UserDTO.class);
    }
}
