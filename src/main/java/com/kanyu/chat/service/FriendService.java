package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.entity.Friendship;

import javax.servlet.http.HttpSession;

public interface FriendService extends IService<Friendship> {

    public void sendFriendRequest(Long requesterId, Long receiverId);
    public void handleRequest(Long requestId, Long userId, boolean accept);


}
