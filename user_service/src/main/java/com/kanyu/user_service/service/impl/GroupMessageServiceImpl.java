package com.kanyu.user_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.user_service.entity.GroupMessage;
import com.kanyu.user_service.mapper.GroupMessageMapper;
import com.kanyu.user_service.service.GroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupMessageServiceImpl extends ServiceImpl<GroupMessageMapper, GroupMessage> implements GroupMessageService {


    @Override
    public GroupMessage getLsatMessage(String groupId) {
        GroupMessage lastMessage = query().eq("group_id", groupId).orderByDesc("create_time").last("limit 1").one();
        return lastMessage;
    }
}
