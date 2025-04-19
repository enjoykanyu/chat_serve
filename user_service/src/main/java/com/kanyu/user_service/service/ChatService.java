package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.MessageDto;
import com.kanyu.user_service.entity.ChatContent;
import com.kanyu.user_service.entity.User;

import javax.servlet.http.HttpSession;

public interface ChatService extends IService<ChatContent> {

    Result oneConent(Long sendUserId, Long receiveUserId, HttpSession session);

    Result allChatUser(User user);

    Result insertChat(ChatContent chatContent);

    Result insertGroupChat(MessageDto messageDto);

    Result groupChat(String group_id, HttpSession session);
}
