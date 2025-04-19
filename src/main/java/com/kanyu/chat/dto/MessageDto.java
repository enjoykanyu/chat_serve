package com.kanyu.chat.dto;

import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import lombok.Data;

import java.util.List;

@Data
//同意封装群聊与单聊请求结构体
public class MessageDto {
    private Integer id;
    private String sendUser;
    private String receiveUser;
    private String message;
    private Integer notRead;
    private String chatType;//消息类型 private 单聊 group 群聊
    private String groupId;// 群聊id
}
