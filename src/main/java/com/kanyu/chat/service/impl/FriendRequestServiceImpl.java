package com.kanyu.chat.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.config.WebSocketServe;
import com.kanyu.chat.dto.FriendRequestResponse;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.FriendRequest;
import com.kanyu.chat.entity.Friendship;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.FriendRequestMapper;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.service.FriendRequestService;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
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
    @Resource
    ChatService chatService;
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
        //存储申请者的验证消息，后续好友通过之后展示在消息记录中
        ChatContent chatContent = new ChatContent();
        chatContent.setSendUserId(requesterId);
        chatContent.setReceiveUserId(receiverId);
        chatContent.setMessage(friendRequest.getReason());
        chatService.insertChat(chatContent);
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
            //查看好友申请关系
        FriendRequest friendRequest = query().eq("requester_id", requestId).eq("receiver_id", userId).one();
        if (friendRequest==null){
            log.info("当前好友无法处理");
            return;
        }
        //1,拒绝，把好友关系改成2
        if (!accept){
            boolean flag = update().eq("requester_id", requestId).eq("receiver_id", userId).setSql("status = "+2).update();
            if (!flag){
                log.info("好友处理关系更新报错");
                return;
            }
            //发送客户端拒绝消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("sendUserId", requestId);
            jsonObject.set("receiveUserId", userId);
            jsonObject.set("content", "拒绝");
            jsonObject.set("type",3);//type=3则表示为好友处理请求
            WebSocketServe.sendMessageApply(requestId,jsonObject.toString());
        }else {
            //2,同意，好友关系改成1
            boolean flag = update().eq("requester_id", requestId).eq("receiver_id", userId).setSql("status = "+1).update();
            if (!flag){
                log.info("好友处理关系更新报错");
                return;
            }
            //存储双向好友关系表
            Friendship friendshipUser = new Friendship();
            friendshipUser.setFriendId(requestId);
            friendshipUser.setUserId(userId);
            friendService.save(friendshipUser);
            Friendship friendshipRequest = new Friendship();
            friendshipRequest.setFriendId(userId);
            friendshipRequest.setUserId(requestId);
            friendService.save(friendshipRequest);
            //发送客户端成功消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("sendUserId", requestId);
            jsonObject.set("receiveUserId", userId);
            jsonObject.set("content", "同意");
            jsonObject.set("type",3);//type=3则表示为好友处理请求
            WebSocketServe.sendMessageApply(requestId,jsonObject.toString());
            //3，增加两者之间的消息
            //3.1申请者的验证消息
//            ChatContent requesterReason = new ChatContent();
//            requesterReason.setSendUserId(requestId);
//            requesterReason.setReceiveUserId(userId);
//            requesterReason.setMessage(friendRequest.getReason());
//            chatService.insertChat(requesterReason);
            //3.2 同意后的系统消息
            ChatContent receiverReason = new ChatContent();
            receiverReason.setReceiveUserId(requestId);
            receiverReason.setSendUserId(userId);
            receiverReason.setMessage("我已同意了你的好友申请");
            chatService.insertChat(receiverReason);
        }

    }



    @Override
    public List<FriendRequestResponse> getAllRequests(Long userId) {
        List<FriendRequest> friendRequests = query().eq("receiver_id", userId).list();
        log.info(friendRequests+"");
        if (friendRequests==null){
            //当前用户无任何新好友处理
            return new ArrayList<>();
        }
        List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
        for (FriendRequest friendDb:friendRequests) {
            //循环遍历出好友的信息这里得仅返回用户的名称、手机号、朋友圈、头像、id，其它的信息不返回
            User request_user = loginService.query().eq("id", friendDb.getRequesterId()).one();
            FriendRequestResponse curFriendApply = new FriendRequestResponse();
            curFriendApply.setApplyUser(request_user);
            curFriendApply.setReason(friendDb.getReason());
            curFriendApply.setStatus(friendDb.getStatus());
            friendRequestResponses.add(curFriendApply);
            log.info("循环"+friendRequestResponses);
        }
        log.info("返回了friendRequestResponses");
        log.info(friendRequestResponses+"");
        return friendRequestResponses;
    }

    @Override
    public List<FriendRequestResponse> getPendingRequests(Long userId) {
        List<FriendRequest> friendRequests = query().eq("receiver_id", userId).eq("status",0).list();
        if (friendRequests==null){
            //当前用户无任何新好友处理
            return new ArrayList<>();
        }
        List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
        for (FriendRequest friendDb:friendRequests) {
            //循环遍历出好友的信息这里得仅返回用户的名称、手机号、朋友圈、头像、id，其它的信息不返回
            User request_user = loginService.query().eq("id", friendDb.getRequesterId()).one();
            FriendRequestResponse curFriendApply = new FriendRequestResponse();
            curFriendApply.setApplyUser(request_user);
            curFriendApply.setReason(friendDb.getReason());
            friendRequestResponses.add(curFriendApply);
        }
        return friendRequestResponses;
    }
}
