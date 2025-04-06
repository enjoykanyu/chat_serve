package com.kanyu.chat.controller;

import com.kanyu.chat.common.Result;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 好友控制器
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/requests")
    public Result sendRequest(
//            @RequestBody FriendRequest request,
//            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        friendService.sendFriendRequest(userId, request.getReceiverId());
        return new Result();
    }

    @PutMapping("/requests/{requestId}")
    public Result handleRequest(
//            @PathVariable Long requestId,
//            @RequestParam boolean accept,
//            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        friendService.handleRequest(requestId, userId, accept);
        return new Result();
    }

//    @GetMapping("/requests")
//    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests(
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        return ResponseEntity.ok(friendService.getPendingRequests(userId));
//    }
}
