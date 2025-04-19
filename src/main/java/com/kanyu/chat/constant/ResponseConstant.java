package com.kanyu.chat.constant;

public class ResponseConstant {
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String PHONE_NOT_FOUND = "手机不存在";
    public static final Integer SUCCESS_CODE = 200;
    public static final Integer USER_NOT_FOUNT_CODE = 410;
    public static final String USER_PASSWORD_NOT_CORRECT = "用户密码错误";
    public static final Integer USER_PASSWORD_NOT_CORRECT_CODE = 415;
    public static final String CODE_NOT_MATCH = "验证码不匹配";
    public static final Integer CODE_NOT_MATCH_OCDE = 416;

    public static final String SERVER_ERROR = "服务器错误，请稍后重试";
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
