package com.atguigu.zhxy.pojo;

import lombok.Data;

/**
 *@Author: feifan
 *@Date: 2022/6/7 10:37
 */
@Data
public class LoginForm {
    private String username;
    private String password;
    private String verifiCode;
    private Integer userType;
}
