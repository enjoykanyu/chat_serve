package com.kanyu.chat.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String password) {
        // 生成一个带有强随机盐的哈希值
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // 检查原始密码是否与哈希值匹配
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    public static void main(String[] args) {
        String t = hashPassword("333");
        System.out.println(t);
        System.out.println(checkPassword("331", t));
    }
}

