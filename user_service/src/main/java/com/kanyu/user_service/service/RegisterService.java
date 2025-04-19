package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.RegisterForm;
import com.kanyu.user_service.entity.User;

import javax.servlet.http.HttpSession;

public interface RegisterService extends IService<User> {
    Result register(RegisterForm registerForm, HttpSession session);
}
