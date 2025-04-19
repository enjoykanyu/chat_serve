package com.kanyu.user_service.dto;

import lombok.Data;

@Data
public class RegisterForm {
    private String userName;
    private String phone;
    private String password;
}
