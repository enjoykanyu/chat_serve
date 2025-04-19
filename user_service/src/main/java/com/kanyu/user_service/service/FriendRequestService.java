package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.dto.FriendRequestResponse;
import com.kanyu.user_service.entity.FriendRequest;

import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {

    public void sendFriendRequest(Long requesterId, Long receiverId,String reason);
    public void handleRequest(Long requestId, Long userId, boolean accept);


    void handleFriendRequest(Long requestId, Long userId, boolean accept);


    List<FriendRequestResponse> getAllRequests(Long userId);

    List<FriendRequestResponse> getPendingRequests(Long userId);
}
