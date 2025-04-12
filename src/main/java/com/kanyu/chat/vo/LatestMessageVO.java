package com.kanyu.chat.vo;

import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LatestMessageVO {
    private Integer chatType; // "private" 或 "group"
//    private Long chatId;     // 私聊时为对方用户ID，群聊时为群ID
    private String chatName; // 聊天名称(对方昵称或群名) 单聊为对方的名称 群聊为群名称
    private String avatar;   // 头像URL 单聊对方的头像 群聊群头像
//    private Long lastMessageId;
    private String lastContent;
    private LocalDateTime lastSendTime;
    private Long lastSenderId; //单聊为最后发消息的人  群聊为最后发消息的人
    private String lastSenderName; //单聊为最后发消息的人
    private Integer unreadCount; // 未读消息数(仅私聊)
    private Group group; //当type=0 不存储 存储群
    private User user; //当type=1 不存储 存储用户





}
