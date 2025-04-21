package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.common.Result;
import com.kanyu.user_service.dto.GroupDto;
import com.kanyu.user_service.entity.Group;


public interface GroupService extends IService<Group> {

    

    Group createGroup(GroupDto dto);

    Result deleteGroup(Group dto);

    Result updateGroup(Group dto);

    Result getGroupMember(String groupId);

    Boolean groupExist(String uuid);

    Result quitGroup(Group dto);
}
