package com.kanyu.chat.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.GroupMessage;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.GroupMessageMapper;
import com.kanyu.chat.service.GroupMemberService;
import com.kanyu.chat.service.GroupMessageService;
import com.kanyu.chat.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class GroupMessageServiceImpl extends ServiceImpl<GroupMessageMapper, GroupMessage> implements GroupMessageService {


    @Override
    public GroupMessage getLsatMessage(String groupId) {
        GroupMessage lastMessage = query().eq("group_id", groupId).orderByDesc("create_time").last("limit 1").one();
        return lastMessage;
    }
}
