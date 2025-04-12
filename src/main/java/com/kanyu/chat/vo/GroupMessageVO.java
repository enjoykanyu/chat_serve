package com.kanyu.chat.vo;

import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMessageVO {
    private Long userId; // 发送消息对象
    private Integer type; // 消息类型 0普通消息 1系统消息
    private String avatar;   // 头像URL 群聊群头像
    private String userAvatar;   // 发送者头像
    private String userName; //发送者的名称
    private String content;
    private LocalDateTime sendTime;

}
