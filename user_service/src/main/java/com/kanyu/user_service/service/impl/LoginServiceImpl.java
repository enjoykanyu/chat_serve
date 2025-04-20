package com.kanyu.user_service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.LoginForm;
import com.kanyu.user_service.entity.User;
import com.kanyu.user_service.mapper.UserMapper;
import com.kanyu.user_service.service.LoginService;
import com.kanyu.user_service.utils.PasswordUtil;
import com.kanyu.user_service.utils.Validate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.kanyu.user_service.constant.RedisConstants.LOGIN_USER_TTL;
import static com.kanyu.user_service.constant.ResponseConstant.LOGIN_USER_KEY;
import static com.kanyu.user_service.constant.ResponseConstant.USER_NOT_FOUND;

@Slf4j
@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, User> implements LoginService {
    //引入redis工具类
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Result sendCode(String phone, HttpSession session) {
        //1，看手机号格式是否正确
        boolean is_valid = Validate.isPhoneInvalid(phone);
        if (is_valid) {
            return Result.fail("手机格式不正确",1000000001);
        }
        //2，看手机号是否存在数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        User user = getOne(queryWrapper.eq("phone", phone));
        //校验手机号是否注册过，未注册过进入注册网页
        if (user == null) {
            return Result.fail("手机未注册",100000008);//这里需前端跳转到注册网页
        }

        //3，生成验证码
        String code = RandomUtil.randomNumbers(6);
        log.info("请求验证码"+code);
        //4，存储进入redis 设置常量 key=登录业务前缀+邮箱 过期时间5分钟
        stringRedisTemplate.opsForValue().set("user_login"+phone,code,300, TimeUnit.SECONDS );
        //5，返回验证码
        HashMap<String,String> codeValue = new HashMap<>();
        codeValue.put("code", code);
        //返回给前端验证码可以打开在新弹窗
        return Result.ok(codeValue);
    }

    @Override
    public Result loginWithPassward(LoginForm loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (Validate.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！",400);
        }
        User user = query().eq("phone", loginForm.getPhone()).one();
        if (user==null){
            //返回用户不存在 这里预期进入注册网页
            return Result.fail(USER_NOT_FOUND,400);
        }
        //校验密码是否正确 这里直接对比原始的密码与加密的密码是否相同
        boolean flag = PasswordUtil.checkPassword(loginForm.getPassword(),user.getPassword());
        //不正确
        if (!flag){
            return Result.fail("USER_PASSWORD_NOT_CORRECT",400);
        }
        // 保存用户信息到 redis中
        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 7.2.将User对象转为HashMap存储
        LoginForm loginForm1 = BeanUtil.copyProperties(user, LoginForm.class);
        System.out.println(loginForm1);
        log.info(loginForm1.toString());
        loginForm1.setCode("");
        Map<String, Object> userMap = BeanUtil.beanToMap(loginForm1, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3.存储
        String tokenKey = LOGIN_USER_KEY + token;
        log.info("tokenKey"+tokenKey);
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        Map<String,Object> result = new HashMap<>();
        result.put("token",token);
        result.put("user",user);

        // 8.返回token
        return Result.ok(result);
    }

    @Override
    public Result loginWithCode(LoginForm loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (Validate.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！",400);
        }
        //3,获取验证码
        String code = stringRedisTemplate.opsForValue().get("user_login" + loginForm.getPhone());
        String code_user = loginForm.getCode();
        //redis拿不到相关验证码，说明过期或者手机号不存在
        if (code==null){
            return Result.fail("用户不存在",100000008);
        }
        //redis保存的验证码与输入的验证码不匹配
        if (!code.equals(code_user)){
            return Result.fail("验证码不正确",100000008);
        }
        User user = query().eq("phone", loginForm.getPhone()).one();
        log.info("user");
        log.info(user.toString());
        if (user==null){
            //返回用户不存在 这里预期进入注册网页
            return Result.fail("用户不存在",100000008);
        }
        // 保存用户信息到 redis中
        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 将User对象转为HashMap存储
        LoginForm loginForm1 = BeanUtil.copyProperties(user, LoginForm.class);
        log.info("loginForm1");
        log.info(loginForm1.toString());
        loginForm1.setCode("");//把null置为""空字符串类型
        //这里之前报错空指针由于 User转成BeanUtil.copyProperties(user, LoginForm.class);User中无code字段 LoginForm有code字段 所有copy过来 code=null 执行BeanUtil.beanToMap报错了
        Map<String, Object> userMap = BeanUtil.beanToMap(loginForm1, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        // 存储
        String tokenKey = LOGIN_USER_KEY + token;
        log.info(userMap.toString());
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        //设置token有效期
        stringRedisTemplate.expire(tokenKey, 300, TimeUnit.MINUTES);

        //返回token
        return Result.ok(token);
    }
}
