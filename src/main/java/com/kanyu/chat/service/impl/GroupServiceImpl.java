package com.kanyu.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.config.WebSocketServe;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.dto.GroupDto;
import com.kanyu.chat.entity.*;
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
        String contnet = own_user.getUserName()+"新建了群聊";
        message.setContent(contnet);
        //系统消息默认发送者为当前创建群聊的人
        message.setSenderId(own_user.getId());
        groupMessageService.save(message);
        //广播群内其它成员更新消息列表
        // 发送好友请求通知
        //群成员新建落库
        for (User member:dto.getMember()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("receiveUserId", member.getId());
            jsonObject.set("content", contnet);
            jsonObject.set("type",5);//type=5则表示为新建群广播通知
            WebSocketServe.sendMessageOnline(member.getId(),jsonObject.toString());
        }


        return group;
    }

    @Override
    public Result deleteGroup(Group dto) {
        boolean is_delete = update().eq("group_id", dto.getGroupId()).setSql("status = 2").update();
        return Result.ok(is_delete);
    }

    @Override
    public Result updateGroup(Group dto) {
        boolean is_update = update().eq("group_id", dto.getGroupId()).setEntity(dto).update();
        return Result.ok(is_update);
    }

    @Override
    public Result getGroupMember(String groupId) {
        List<GroupMember> groupMemberList = groupMemberService.query().eq("group_id", groupId).list();
        List<User> result = new ArrayList<>();
        for (GroupMember member:groupMemberList) {
            User user = loginService.query().eq("id", member.getUserId()).one();
            result.add(user);
        }
        return Result.ok(result);
    }

    /*
     * 查询当前群聊是否被解散
     * */
    @Override
    public Boolean groupExist(String uuid) {
        Group group = query().eq("group_id", uuid).one();
        if (group.getStatus()==1){
            return true;
        }else {
            return false;
        }
    }
    @Override
    public Result quitGroup(Group dto) {
        boolean is_quit = groupMemberService.update().eq("group_id", dto.getGroupId()).eq("user_id", UserHolder.getUser().getId()).update();
        return Result.ok(is_quit);
    }
}
