package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.entity.GroupMember;

import java.util.List;


public interface GroupMemberService extends IService<GroupMember> {


    void createGroupMember(String uuid, Long id,Integer role);

    List<GroupMember> selectGroups(Long user);


    Boolean isGroupMember(String uuid,Long userId);


}
