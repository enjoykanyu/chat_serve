package com.kanyu.user_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.RegisterForm;
import com.kanyu.user_service.entity.User;
import com.kanyu.user_service.mapper.UserMapper;
import com.kanyu.user_service.service.RegisterService;
import com.kanyu.user_service.utils.PasswordUtil;
import com.kanyu.user_service.utils.Validate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Slf4j
@Service
public class RegistererviceImpl extends ServiceImpl<UserMapper, User> implements RegisterService {
    //引入redis工具类
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result register(RegisterForm registerForm, HttpSession session) {
        // 1.校验手机号
        String phone = registerForm.getPhone();
        if (Validate.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！",400);
        }
        User user_query = query().eq("phone", phone).one();
        if (user_query!=null){
            return Result.fail("用户已注册过，请登录",400);
        }
        registerForm.setPassword(PasswordUtil.hashPassword(registerForm.getPassword()));
        User user = new User();
        user.setPassword(registerForm.getPassword());
        user.setUserName(registerForm.getUserName());
        user.setPhone(registerForm.getPhone());
        save(user);
        return Result.ok();
    }
}
