package com.kanyu.user_service.dto;

import com.kanyu.user_service.entity.User;
import lombok.Data;


@Data
public class FriendShipDto {
    private User user;
    private User friendUser;
}
