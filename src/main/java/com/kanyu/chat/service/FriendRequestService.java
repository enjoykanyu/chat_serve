package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.Friendship;

import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {

    public void sendFriendRequest(Long requesterId, Long receiverId,String reason);
    public void handleRequest(Long requestId, Long userId, boolean accept);


    void handleFriendRequest(Long requestId, Long userId, boolean accept);


    List<FriendRequest> getAllRequests(Long userId);
}
