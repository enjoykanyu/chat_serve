package com.kanyu.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.Group;
import com.kanyu.chat.entity.GroupMessage;


public interface GroupMessageService extends IService<GroupMessage> {


    GroupMessage getLsatMessage(String groupId);
}
