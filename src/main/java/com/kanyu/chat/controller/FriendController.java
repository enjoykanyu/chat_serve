package com.kanyu.chat.controller;

import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.FriendRequestDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.Friendship;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.service.FriendRequestService;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.utils.UserHolder;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 好友控制器
 */
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    //好友服务
    @Resource
    private FriendService friendService;
    //好友申请服务
    @Resource
    private FriendRequestService friendRequestService;

    // 发送好友请求
    @PostMapping("/friend-apply")
    public Result sendFriendRequest(
            @RequestBody FriendRequestDto dto
    ) {
        friendRequestService.sendFriendRequest(UserHolder.getUser().getId(), dto.getApplyUserId(), dto.getReason());
        return Result.ok();
    }

    // 处理好友请求
    @PutMapping("/requests/{requestId}")
    public Result handleRequest(
            @PathVariable Long requestId,
            @RequestParam boolean accept //好友仅拒绝和同意 暂无拒绝理由
    ) {
        Long userId = UserHolder.getUser().getId();
        friendRequestService.handleFriendRequest(requestId, userId, accept);
        return Result.ok();
    }

    // 获取好友列表
    @GetMapping("/all")
    public List<FriendShipDto> getFriends(
    ) {
        Long userId = UserHolder.getUser().getId();
        return friendService.getFriends(userId);
    }

    // 获取所有的好友请求
    @GetMapping("/requests/all")
    public List<FriendRequest> getPendingRequests(
    ) {
        Long userId = UserHolder.getUser().getId();
        return friendRequestService.getAllRequests(userId);
    }

    // 根据指定手机号搜索用户
    @GetMapping("/search/user") //精准匹配仅可搜索手机号
    public Result searchUser(@RequestParam("phone") String phone, HttpSession session) {
        Long userId = UserHolder.getUser().getId();
        return friendService.searchUser(phone);
    }

    // 根据指定手机号搜索用户判断是否为当前用户的好友
    @PostMapping("/search/isFriend") //精准匹配仅可搜索手机号
    public Result searchUser(@RequestBody User user, HttpSession session) {
        Long userId = UserHolder.getUser().getId();
        return friendService.isFriend(userId,user.getId());
    }

}
