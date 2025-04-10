package com.kanyu.chat.controller;

import cn.hutool.log.Log;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.service.LoginService;
import com.kanyu.chat.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 私信消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {
    //验证码动态登录前置发送验证码
    @Resource
    ChatService chatService;

    @Resource
    LoginService loginService;
    //拿到当前用户下和对方的全部聊天内容
    @GetMapping("oneChat")
    public Result oneConent(@RequestParam("sendUserId") String sendUserId, @RequestParam("receiveUserId") String receiveUserId, HttpSession session) {
        //打印日志
        log.info("发送用户"+sendUserId);
        log.info("接收用户"+receiveUserId);
        Result result = chatService.oneConent(Long.parseLong(sendUserId),Long.parseLong(receiveUserId),session);
        return result;
    }
    //拿到当前用户下和对方的全部聊天内容
    @GetMapping("allChat")
    public Result allConent(@RequestParam("sendUserId") String sendUserId, @RequestParam("searchUserName") String searchUserName, HttpSession session) {
        //打印日志

        log.info("发送用户"+sendUserId);
        log.info("接收用户"+searchUserName);
        User user = loginService.query().eq("user_name", searchUserName).one();
        Long receiveUserId;
        if (user == null){
            receiveUserId = 0L;
        }else {
            receiveUserId = user.getId();
        }
        log.info("接收用户id"+user.getId());
        Result result = chatService.oneConent(Long.parseLong(sendUserId),receiveUserId,session);
        return result;
    }

    //发送消息
    @PostMapping("send")
    public Result send(@RequestBody ChatContent chatContent, HttpSession session) {
        //打印日志
        log.info("发送用户消息"+chatContent);
//        chatService.save(chatContent);
        return chatService.insertChat(chatContent);
//        return Result.ok();
    }

    //拿到与当前用户发过消息的所有用户列表，展示最后一条消息，没有读过的消息数量，最后的消息谁发送的
    @GetMapping("allChatUser")
    public Result allChatUser(HttpSession session) {
        //打印日志
        User user = UserHolder.getUser();
        log.info("当前用户"+user);
        Result result = chatService.allChatUser(user);
        return result;
    }
}
