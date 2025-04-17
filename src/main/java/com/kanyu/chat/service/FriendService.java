package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.Friendship;
import com.kanyu.chat.entity.User;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface FriendService extends IService<Friendship> {


    List<FriendShipDto> getFriends(Long userId);

    Result searchUser(String phone);

    Result isFriend(Long userId, Long id);

    Result deleteFriend(Long userId, Long friendId);

    Result blackFriend(Long userId, Long friendId);
}
