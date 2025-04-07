package com.kanyu.chat.dto;

import com.kanyu.chat.entity.User;
import lombok.Data;


@Data
public class FriendRequestDto {
    private String resaon;
    private Long sendUserId;
    private Long recieiveUserId;
    private Integer notRead;
}
