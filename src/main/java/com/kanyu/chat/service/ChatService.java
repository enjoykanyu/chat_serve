package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;


import javax.servlet.http.HttpSession;

public interface ChatService extends IService<ChatContent> {

    Result oneConent(Long sendUserId, Long receiveUserId, HttpSession session);

    Result allChatUser(User user);

    Result insertChat(ChatContent chatContent);
}
