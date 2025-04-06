package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.entity.Friendship;
import com.kanyu.chat.mapper.FriendMapper;
import com.kanyu.chat.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friendship> implements FriendService {


//    private final FriendshipRepository friendshipRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        // 验证不能添加自己
        if (requesterId.equals(receiverId)) {
//            throw new BusinessException("不能添加自己为好友");
            return;
        }
        
        // 检查是否已存在关系
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
}
