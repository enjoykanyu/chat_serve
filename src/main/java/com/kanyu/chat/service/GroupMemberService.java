package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.GroupMember;

import java.util.List;


public interface GroupMemberService extends IService<GroupMember> {


    void createGroupMember(String uuid, Long id,Integer role);

    List<GroupMember> selectGroups(Long user);


    Boolean isGroupMember(String uuid,Long userId);


}
