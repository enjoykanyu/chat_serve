package com.kanyu.user_service.controller;

import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.FriendRequestDto;
import com.kanyu.user_service.dto.FriendRequestResponse;
import com.kanyu.user_service.dto.FriendShipDto;
import com.kanyu.user_service.entity.User;
import com.kanyu.user_service.service.FriendRequestService;
import com.kanyu.user_service.service.FriendService;
import com.kanyu.user_service.utils.UserHolder;
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
    public List<FriendRequestResponse> getAllRequests(
    ) {
        Long userId = UserHolder.getUser().getId();
        return friendRequestService.getAllRequests(userId);
    }

    // 获取所有未处理好友请求数量
    @GetMapping("/requests/all/getPending")
    public List<FriendRequestResponse> getPendingRequests(
    ) {
        Long userId = UserHolder.getUser().getId();
        return friendRequestService.getPendingRequests(userId);
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


    // 删除好友
    @PostMapping("/delete")
    public Result deleteFriend(@RequestParam Long friendId, HttpSession session) {
        Long userId = UserHolder.getUser().getId();
        return friendService.deleteFriend(userId,friendId);
    }

    // 拉黑好友
    @PostMapping("/black")
    public Result blackFriend(@RequestParam Long friendId, HttpSession session) {
        Long userId = UserHolder.getUser().getId();
        return friendService.blackFriend(userId,friendId);
    }

}
