package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.dto.MessageDto;
import com.kanyu.chat.entity.*;
import com.kanyu.chat.mapper.ChatMapper;
import com.kanyu.chat.mapper.MomentsMapper;
import com.kanyu.chat.service.*;
import com.kanyu.chat.vo.GroupMessageVO;
import com.kanyu.chat.vo.LatestMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MomentsServiceImpl extends ServiceImpl<MomentsMapper, Moments> implements MomentsService {

    @Resource
    LoginService loginService;

    @Resource
    GroupMessageService groupMessageService;
    @Resource
    GroupService groupService;
    @Override
    public Result oneConent(Long sendUserId, Long receiveUserId, HttpSession session) {
        //拿到所有和当前用户以及对方的消息记录 根据新建时间进行排序
        return Result.ok();
    }

    @Override
    public Result allChatUser(User user) {
       return Result.ok();
    }

    @Override
    public Result insertGroupChat(MessageDto messageDto) {
        return Result.ok();
    }

    @Override
    public Result insertChat(ChatContent chatContent) {

        return Result.ok();
    }

    @Override
    public Result groupChat(String group_id, HttpSession session) {
        Group group = groupService.query().eq("group_id", group_id).one();
        List<GroupMessage> groupMessages = groupMessageService.query().eq("group_id", group.getGroupId()).list();
        List<GroupMessageVO> result = new ArrayList<>();
        loginService.query().eq("id",groupMessages);
        for (GroupMessage groupMessage: groupMessages) {
            User user = loginService.query().eq("id", groupMessage.getSenderId()).one();
            GroupMessageVO groupMessageVO = new GroupMessageVO();
            groupMessageVO.setContent(groupMessage.getContent());
            groupMessageVO.setSendTime(groupMessage.getCreateTime());
            groupMessageVO.setType(groupMessage.getType());
            groupMessageVO.setAvatar(group.getAvatar());
            groupMessageVO.setUserId(groupMessage.getSenderId());
            groupMessageVO.setUserAvatar(user.getAvatar());
            groupMessageVO.setUserName(user.getUserName());
            result.add(groupMessageVO);
        }
        return Result.ok(result);
    }
}
