package com.kanyu.chat.controller;

import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.LoginForm;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.service.LoginService;
import com.kanyu.chat.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class LoginController {

    //验证码动态登录前置发送验证码
    @Resource
    LoginService loginService;
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // TODO 发送短信验证码并保存验证码
        //打印日志
        log.info("前端请求手机"+phone);
        Result result = loginService.sendCode(phone,session);
        return result;
    }

    //手机号和密码登录
    @PostMapping("login")
    public Result login(@RequestBody LoginForm loginForm, HttpSession session) {
        //打印日志
        log.info("前端请求登录信息"+loginForm);
        Result result = loginService.loginWithPassward(loginForm,session);
        return result;
    }
    //验证码登录
    @PostMapping("login/code")
    public Result loginWithCode(@RequestBody LoginForm loginForm, HttpSession session) {
        //打印日志
        log.info("前端请求登录信息"+loginForm);
        Result result = loginService.loginWithCode(loginForm,session);
        return result;
    }

    @GetMapping("me")
    public Result me(HttpSession session) {
        // TODO 发送短信验证码并保存验证码
        //打印日志
        String userName = UserHolder.getUser().getUserName();
        log.info("前端请求用户"+userName);
        Map<String,String> user = new HashMap<>();
        user.put("username",userName);
        return Result.ok(user);
    }
    @GetMapping("login/user")
    public Result loginUser(HttpSession session) {
        //打印日志
        User user = UserHolder.getUser();
        log.info("前端请求用户"+user);
        return Result.ok(user);
    }

    @GetMapping("userInfo")
    public Result userInfo(HttpSession session) {
        //打印日志
        String userName = UserHolder.getUser().getUserName();
        log.info("前端请求用户"+userName);
        Map<String,String> user = new HashMap<>();
        user.put("username",userName);
        user.put("userId",UserHolder.getUser().getId()+"");
        return Result.ok(user);
    }

    @GetMapping("search")
    public Result userInfo(@RequestParam("userId") String loginUserId,
                           @RequestParam("searchUserName") String searchUserName,HttpSession session) {
//                [{"from_user":{"userIdD":1021,"userName":"3"},"sendUser":{"userID":1021,"userName":"3"},"message":"发送的消息"}]
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String,Object> value = new HashMap<>();
        Map<String,Object> value_index = new HashMap<>();
        value.put("userID",1021);
        value.put("userName","3");
        value_index.put("from_user",value);
        value.replace("userID",1035);
        value.replace("userName","33");
        value_index.put("sendUser",value);
        value_index.put("message","发送的消息");
        result.add(value_index);
        return Result.ok(result);
    }

}
