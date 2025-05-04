package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.RegisterForm;
import com.kanyu.chat.entity.User;

import jakarta.servlet.http.HttpSession;

public interface RegisterService extends IService<User> {
    Result register(RegisterForm registerForm, HttpSession session);
}
