package com.kanyu.chat.controller;

import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.RegisterForm;
import com.kanyu.chat.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Resource
    RegisterService registerService;
    @PostMapping("user")
    public Result sendCode(@RequestBody RegisterForm registerForm, HttpSession session) {
        //打印日志
        log.info("用户注册信息"+ registerForm.toString());
        Result result = registerService.register(registerForm,session);
        return result;
    }



}
