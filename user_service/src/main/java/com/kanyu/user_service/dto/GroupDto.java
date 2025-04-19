package com.kanyu.user_service.dto;

import com.kanyu.user_service.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class GroupDto {
    private String groupName;
    private Long ownerId;
    private List<User> member;
}
