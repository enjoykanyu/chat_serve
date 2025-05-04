package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.Friendship;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.FriendMapper;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.service.LoginService;
import com.kanyu.chat.utils.UserHolder;
//import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friendship> implements FriendService {



//    private final FriendshipRepository friendshipRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate messagingTemplate;

    @Resource
    LoginService loginService;
    @Override
    public List<FriendShipDto> getFriends(Long userId) {
        List<Friendship> friendshipList = query().eq("user_id", userId).list();
        List<FriendShipDto> friendShipList = new ArrayList<>();
        for (Friendship friendship : friendshipList){
            FriendShipDto cur_friend = new FriendShipDto();
            User user = loginService.query().eq("id", friendship.getFriendId()).one();
            cur_friend.setFriendUser(user);
            cur_friend.setUser(UserHolder.getUser());
            friendShipList.add(cur_friend);
        }
        return friendShipList;
    }

    @Override
    public Result searchUser(String phone) {
        User search_user = loginService.query().eq("phone", phone).one();
        if (search_user==null){
            return Result.fail("用户不存在",400);
        }
        return Result.ok(search_user);
    }

    @Override
    public Result isFriend(Long userId, Long friend_id) {
        Friendship one = query().eq("user_id", userId).eq("friend_id", friend_id).one();
        log.info("查询到的用户好友"+one);
        if (one==null){
            return Result.fail("当前用户不是您的好友",400);
        }
        if (one.getStatus() == 1){
            return Result.ok(true);
        }else if (one.getStatus() ==2){
            return Result.fail("当前好友已被您删除",100007);
        }else if (one.getStatus() ==3){
            return Result.fail("当前好友已被您拉入黑名单",100008);
        }else {
            return Result.fail("系统错误",400);
        }
    }

    @Override
    public Result deleteFriend(Long userId, Long friendId) {
        boolean is_update = update().eq("friend_id", friendId).eq("user_id", userId).setSql("status = 2").update();
        if (is_update){
            return Result.ok();
        }
        return Result.fail("系统服务错误",400);
    }

    @Override
    public Result blackFriend(Long userId, Long friendId) {
        boolean is_update = update().eq("friend_id", friendId).eq("user_id", userId).setSql("status = 3").update();
        if (is_update){
            return Result.ok();
        }
        return Result.fail("系统服务错误",400);
    }


}
