package com.kanyu.chat.dto;

import com.kanyu.chat.entity.User;
import lombok.Data;


@Data
public class FriendRequestDto {
    private String reason;
    private Long applyUserId;
}
