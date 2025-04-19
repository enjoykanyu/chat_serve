package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.Group;


public interface GroupService extends IService<Group> {

    

    Group createGroup(GroupDto dto);

    Result deleteGroup(Group dto);

    Result updateGroup(Group dto);

    Result getGroupMember(String groupId);

    Boolean groupExist(String uuid);
}
