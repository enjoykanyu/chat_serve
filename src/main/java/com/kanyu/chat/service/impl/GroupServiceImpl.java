package com.kanyu.chat.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.GroupMessage;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.ChatMapper;
import com.kanyu.chat.mapper.GroupMapper;
import com.kanyu.chat.service.*;
import com.kanyu.chat.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {


    @Resource
    GroupMemberService groupMemberService;
    @Resource
    GroupMessageService groupMessageService;
    @Resource
    LoginService loginService;
    @Override
    public Group createGroup(GroupDto dto) {
        //群组服务新建群
        Group group = new Group();
        group.setGroupName(dto.getGroupName());
        group.setOwnerId(UserHolder.getUser().getId());
        String uuid = UUID.randomUUID().toString();
        group.setGroupId(uuid);
        save(group);
        //群主新建落库
        groupMemberService.createGroupMember(uuid,UserHolder.getUser().getId(),2);
        //群成员新建落库
        for (User member:dto.getMember()) {
            groupMemberService.createGroupMember(uuid,member.getId(),0);
        }
        //新建系统群聊消息
        GroupMessage message = new GroupMessage();
        message.setGroupId(uuid);
        //设置系统消息
        message.setType(1);
        User own_user = UserHolder.getUser();
        message.setContent(own_user.getUserName()+"新建了群聊");
        //系统消息默认发送者为当前创建群聊的人
        message.setSenderId(own_user.getId());
        groupMessageService.save(message);
        return group;
    }

    @Override
    public Result deleteGroup(Group dto) {
        return null;
    }

    @Override
    public Result updateGroup(Group dto) {
        return null;
    }
}
