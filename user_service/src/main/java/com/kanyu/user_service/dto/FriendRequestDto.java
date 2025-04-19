package com.kanyu.user_service.dto;

import lombok.Data;


@Data
public class FriendRequestDto {
    private String reason;
    private Long applyUserId;
}
