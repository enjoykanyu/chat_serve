package com.kanyu.user_service.dto;

import lombok.Data;

@Data
public class LoginForm {
    private Long id;
    private String userName;
    private String phone;
    private String password;
    private String code;
    private String avatar;
}
