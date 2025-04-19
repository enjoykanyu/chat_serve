package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.LoginForm;
import com.kanyu.user_service.entity.User;

import javax.servlet.http.HttpSession;

public interface LoginService extends IService<User> {
    Result sendCode(String phone, HttpSession session);

    Result loginWithPassward(LoginForm loginForm, HttpSession session);

    Result loginWithCode(LoginForm loginForm, HttpSession session);
}
