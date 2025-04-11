package com.kanyu.chat.dto;

import com.kanyu.chat.entity.User;
import lombok.Data;


@Data
public class FriendRequestResponse {
    private String reason;
    private User applyUser;
    private Integer status;//好友请求状态
}
