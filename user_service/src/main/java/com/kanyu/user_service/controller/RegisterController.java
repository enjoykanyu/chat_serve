package com.kanyu.user_service.controller;

import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.RegisterForm;
import com.kanyu.user_service.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

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
