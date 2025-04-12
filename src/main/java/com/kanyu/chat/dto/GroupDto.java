package com.kanyu.chat.dto;

import com.kanyu.chat.entity.GroupMember;
import com.kanyu.chat.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class GroupDto {
    private String groupName;
    private Long ownerId;
    private List<User> member;
}
