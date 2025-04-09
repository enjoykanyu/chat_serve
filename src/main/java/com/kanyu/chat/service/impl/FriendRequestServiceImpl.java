package com.kanyu.chat.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.config.WebSocketServe;
import com.kanyu.chat.dto.FriendRequestResponse;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.FriendRequestMapper;
import com.kanyu.chat.service.FriendRequestService;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {



//    private final FriendshipRepository friendshipRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate messagingTemplate;

    @Resource
    FriendService friendService;
    @Resource
    LoginService loginService;
    @Transactional
    public void sendFriendRequest(Long requesterId, Long receiverId,String reason) {
        // 验证不能添加自己
        if (requesterId.equals(receiverId)) {
//            throw new BusinessException("不能添加自己为好友");
            return;
        }

        // 检查是否已存在关系
        Result friend = friendService.isFriend(requesterId, receiverId);
        if (friend.getSuccess()){
            log.info("该用户已为自己的好友，请勿重复添加好友");
            return;
        }

        //保存申请请求记录
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequesterId(requesterId);
        friendRequest.setReceiverId(receiverId);
        friendRequest.setReason(reason);
        save(friendRequest);
        //向客户端用户B发送好友请求
        // 发送好友请求通知
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("sendUserId", requesterId);
        jsonObject.set("receiveUserId", receiverId);
        jsonObject.set("content", reason);
        jsonObject.set("type",1);//type=1则表示为好用申请请求
        WebSocketServe.sendMessageOnline(receiverId,jsonObject.toString());

//        Optional<Friendship> existing = friendshipRepository
//            .findByRequesterIdAndReceiverId(requesterId, receiverId);
//        if (existing.isPresent()) {
//            throw new BusinessException("请勿重复发送请求");
//        }
//
//        User requester = userRepository.findById(requesterId)
//            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
//        User receiver = userRepository.findById(receiverId)
//            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
//
//        Friendship friendship = new Friendship();
//        friendship.setRequester(requester);
//        friendship.setReceiver(receiver);
//        friendship.setStatus(FriendStatus.PENDING);
//        friendshipRepository.save(friendship);
//
//        // 发送实时通知
//        messagingTemplate.convertAndSendToUser(
//            receiverId.toString(),
//            "/queue/friend-requests",
//            new FriendRequestDTO(requester, friendship)
//        );
    }

    @Transactional
    public void handleRequest(Long requestId, Long userId, boolean accept) {
//        Friendship friendship = friendshipRepository.findById(requestId)
//            .orElseThrow(() -> new ResourceNotFoundException("请求不存在"));
//
//        if (!friendship.getReceiver().getId().equals(userId)) {
//            throw new BusinessException("无权处理该请求");
//        }
//
//        friendship.setStatus(accept ? FriendStatus.ACCEPTED : FriendStatus.REJECTED);
//        friendshipRepository.save(friendship);
//
//        // 通知请求处理结果
//        messagingTemplate.convertAndSendToUser(
//            friendship.getRequester().getId().toString(),
//            "/queue/friend-results",
//            new FriendResponseDTO(friendship)
//        );
    }

    @Override
    public void handleFriendRequest(Long requestId, Long userId, boolean accept) {

    }



    @Override
    public List<FriendRequest> getAllRequests(Long userId) {
        List<FriendRequest> friendRequests = query().eq("user_id", userId).list();
        if (friendRequests==null){
            //当前用户无任何新好友处理
            return new ArrayList<>();
        }
        return friendRequests;
    }
}
