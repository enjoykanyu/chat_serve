package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.FriendShipDto;
import com.kanyu.user_service.entity.Friendship;

import java.util.List;

public interface FriendService extends IService<Friendship> {


    List<FriendShipDto> getFriends(Long userId);

    Result searchUser(String phone);

    Result isFriend(Long userId, Long id);

    Result deleteFriend(Long userId, Long friendId);

    Result blackFriend(Long userId, Long friendId);
}
