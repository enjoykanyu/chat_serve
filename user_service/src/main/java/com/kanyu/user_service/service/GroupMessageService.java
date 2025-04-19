package com.kanyu.user_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kanyu.user_service.entity.GroupMessage;


public interface GroupMessageService extends IService<GroupMessage> {


    GroupMessage getLsatMessage(String groupId);
}
