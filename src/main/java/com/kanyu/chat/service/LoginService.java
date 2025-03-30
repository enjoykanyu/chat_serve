package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.LoginForm;
import com.kanyu.chat.entity.User;
import javax.servlet.http.HttpSession;

public interface LoginService extends IService<User> {
    Result sendCode(String phone, HttpSession session);

    Result loginWithPassward(LoginForm loginForm, HttpSession session);

    Result loginWithCode(LoginForm loginForm, HttpSession session);
}
