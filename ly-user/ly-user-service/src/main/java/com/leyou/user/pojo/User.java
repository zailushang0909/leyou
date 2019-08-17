package com.leyou.user.pojo;

import com.leyou.common.utils.constants.RegexPatterns;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;


@Data
@Table(name="tb_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Pattern(regexp = RegexPatterns.USERNAME_REGEX, message = "用户名，格式为4~30位字母、数字、下划线")
    @NotNull(message = "用户名不能为空")
    private String username;
    @Length(min=4, max = 30, message ="密码，格式为4~30位字母、数字、下划线" )
    @NotNull(message = "密码不能为空")
    private String password;
    @Pattern(regexp = RegexPatterns.PHONE_REGEX, message = "手机号码格式不正确")
    @NotNull(message = "手机号不能为空")
    private String phone;
    private Date createTime;
    private Date updateTime;
}