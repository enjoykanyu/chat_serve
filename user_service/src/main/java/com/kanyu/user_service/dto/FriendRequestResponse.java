package com.kanyu.user_service.dto;

import com.kanyu.user_service.entity.User;
import lombok.Data;


@Data
public class FriendRequestResponse {
    private String reason;
    private User applyUser;
    private Integer status;//好友请求状态
}
