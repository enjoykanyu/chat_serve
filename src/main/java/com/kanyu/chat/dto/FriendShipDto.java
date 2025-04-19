package com.kanyu.chat.dto;

import com.kanyu.chat.entity.User;
import lombok.Data;


@Data
public class FriendShipDto {
    private User user;
    private User friendUser;
}
