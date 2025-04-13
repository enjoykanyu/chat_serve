package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.dto.MessageDto;
import com.kanyu.chat.entity.*;
import com.kanyu.chat.mapper.ChatMapper;
import com.kanyu.chat.service.*;
import com.kanyu.chat.vo.GroupMessageVO;
import com.kanyu.chat.vo.LatestMessageVO;
import jodd.util.ArraysUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, ChatContent> implements ChatService {

    @Resource
    LoginService loginService;
    @Resource
    FriendService friendService;
    @Resource
    GroupMemberService groupMemberService;
    @Resource
    GroupMessageService groupMessageService;
    @Resource
    GroupService groupService;
    @Override
    public Result oneConent(Long sendUserId, Long receiveUserId, HttpSession session) {
        User sendUser = loginService.query().eq("id", sendUserId).one();
        User receiveUser = loginService.query().eq("id", receiveUserId).one();
        List<ChatContentDto> all_user_chat = new ArrayList<>();

        if (receiveUserId == 0L){
            //查询所有最近和当前用户聊天过的全部信息
        }
        else {
            //拿到所有的当前用户发送给对方的消息
            List<ChatContent> send_user_content_list = query().eq("send_user_id", sendUserId).eq("receive_user_id", receiveUserId).list();
            //拿到所有的对方发送给当前用户的消息
            List<ChatContent> receive_user_content_list = query().eq("send_user_id", receiveUserId).eq("receive_user_id", sendUserId).list();

            ArrayList<ChatContent> chatcontens = new ArrayList<>();
            //多线程
            List<ChatContent> allContent = Collections.synchronizedList(chatcontens);
            allContent.addAll(send_user_content_list);
            allContent.addAll(receive_user_content_list);
            List<ChatContent> result_content = allContent.stream().sorted(Comparator.comparing(ChatContent::getCreateTime)).collect(Collectors.toList());
            ChatContentDto chatContentDto = new ChatContentDto();
            chatContentDto.setChatContents(result_content);
            chatContentDto.setSendUser(sendUser);
            chatContentDto.setRecieiveUser(receiveUser);
            all_user_chat.add(chatContentDto);
        }

        //拿到所有和当前用户以及对方的消息记录 根据新建时间进行排序
        return Result.ok(all_user_chat);









    }

    @Override
    public Result allChatUser(User user) {
        //1,找到当前用户的所有联系人，不包括拉黑和删除的 当前用户主动拉黑和删除不展示 被动删除和拉黑会展示
        List<FriendShipDto> friends = friendService.getFriends(user.getId());
        //2,循环遍历所有联系人的消息且与当前用户主动发送的时间对比哪个更新
        List<HashMap<String,Object>> result_list = new ArrayList<>();
        List<LatestMessageVO> response = new ArrayList<>();
        //3，拿到当前用户所有的群组
        List<GroupMember> groups = groupMemberService.selectGroups(user.getId());
        log.info("当前用户加入的群聊"+groups);
        for (GroupMember cur_groups :groups) {
            HashMap<String,Object> group_last_message = new HashMap<>(); //内层循环防止覆盖最后消息 bugfix
            //拿到当前群组的最后一条消息
            GroupMessage lastMessage = groupMessageService.getLsatMessage(cur_groups.getGroupId());
            //拿到这个群消息
            Group group = groupService.query().eq("group_id", cur_groups.getGroupId()).one();
            User lastUser = loginService.query().eq("id", lastMessage.getSenderId()).one();
            log.info("lastUser"+lastUser);
            log.info("当前用户群聊消息"+lastMessage);
            LatestMessageVO latestMessageVO = new LatestMessageVO();
            latestMessageVO.setLastSendTime(lastMessage.getCreateTime());//存储消息最后发送时间
            group_last_message.put("time",lastMessage.getCreateTime());//存储消息最后发送时间
            latestMessageVO.setLastContent(lastMessage.getContent());//存储最后一条消息
            group_last_message.put("cotnet",lastMessage.getContent());//存储最后一条消息
            group_last_message.put("unread",0);//存储当前用户的未读数量
            latestMessageVO.setUnreadCount(0);//存储当前用户的未读数量
            group_last_message.put("group_last_user",lastMessage.getSenderId());//拿到当前联系人
            latestMessageVO.setLastSenderId(lastMessage.getSenderId());//拿到当前联系人
            latestMessageVO.setChatType(1);// 群聊信息类型1
            group_last_message.put("group",group);
            group_last_message.put("message_type",1);// 群聊信息类型1
            latestMessageVO.setAvatar(group.getAvatar());//存储群聊头像
            latestMessageVO.setChatName(group.getGroupName());//存储群聊名称
            latestMessageVO.setLastSenderName(lastUser.getUserName());//存储最后消息名称
            latestMessageVO.setGroup(group);//存储当前群
            result_list.add(group_last_message);
            response.add(latestMessageVO);
            log.info("当前用户群聊消息result_list"+result_list);
        }
        for (FriendShipDto friend : friends) {
            HashMap<String,Object> result = new HashMap<>(); //内层循环防止覆盖最后消息 bugfix
            LatestMessageVO latestMessageVO = new LatestMessageVO();
            log.info("当前循环到了用户"+friend);
            //拿到当前用户发送给对方的最后一条消息
            ChatContent cur_cotent = query().eq("send_user_id", user.getId()).eq("receive_user_id", friend.getFriendUser().getId()).orderByDesc("create_time").last("limit 1").one();
            log.info("当前用户发送给对方的最后一条消息"+cur_cotent);
            //拿到当前对方发送给当前用户的最后一条消息
            ChatContent reciver_cotent = query().eq("receive_user_id", user.getId()).eq("send_user_id", friend.getFriendUser().getId()).orderByDesc("create_time").last("limit 1").one();
            log.info("当前用户发送给对方的最后一条消息"+reciver_cotent);
            ChatContent actual_cotent = cur_cotent; //实际最靠后的消息对象
            Integer unread =0;
            if (cur_cotent==null || reciver_cotent==null){ //当前用户没有发送消息给对方
                if (cur_cotent == null && reciver_cotent==null ){
                    continue; //跳过当前联系人的消息
                }else if (cur_cotent == null){
                    actual_cotent = reciver_cotent;
                    unread = query().eq("receive_user_id", user.getId()).eq("send_user_id", friend.getFriendUser().getId()).eq("is_read", 1).count();
                    User actual_user = loginService.query().eq("id", actual_cotent.getReceiveUserId()).one();
                    result.put("user",actual_user);//存储谁的消息为最后一条
                    latestMessageVO.setLastSenderName(actual_user.getUserName()); //存储最后一条的用户名称
                    latestMessageVO.setLastContent(actual_cotent.getMessage());//存储最后一条的消息
                }else {
                    User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                    result.put("user",actual_user);//存储谁的消息为最后一条
                    latestMessageVO.setLastSenderName(actual_user.getUserName()); //存储最后一条的用户名称
                    latestMessageVO.setLastContent(actual_cotent.getMessage());//存储最后一条的消息
                }
            } else if (cur_cotent.getCreateTime().isBefore(reciver_cotent.getCreateTime())){ //当前用户发送的消息更靠前 则对方的消息更新靠后
                actual_cotent = reciver_cotent;
                unread = query().eq("receive_user_id", user.getId()).eq("send_user_id", friend.getFriendUser().getId()).eq("is_read", 1).count();
                User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                result.put("user",actual_user);//存储谁的消息为最后一条
                latestMessageVO.setLastSenderName(actual_user.getUserName()); //存储最后一条的用户名称
                latestMessageVO.setLastContent(actual_cotent.getMessage());//存储最后一条的消息
            }else{
                User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                result.put("user",actual_user);//存储谁的消息为最后一条
                latestMessageVO.setLastSenderName(actual_user.getUserName()); //存储最后一条的用户名称
                latestMessageVO.setLastContent(actual_cotent.getMessage());//存储最后一条的消息

            }
            result.put("time",actual_cotent.getCreateTime());//存储消息最后发送时间
            result.put("cotnet",actual_cotent.getMessage());//存储最后一条消息
            result.put("unread",unread);//存储当前用户的未读数量
            result.put("receiver_user",friend.getFriendUser());//拿到当前联系人
            result.put("message_type",0);// 单聊信息类型0
            latestMessageVO.setLastSendTime(actual_cotent.getCreateTime()); //存储消息最后发送时间
            latestMessageVO.setChatType(0);
            latestMessageVO.setUnreadCount(0);
            latestMessageVO.setChatName(friend.getFriendUser().getUserName());
            latestMessageVO.setAvatar(friend.getFriendUser().getAvatar());
            latestMessageVO.setUser(friend.getFriendUser());//存储当前用户聊天对象
            log.info("result"+friend.getFriendUser());
            log.info("latestMessageVO"+latestMessageVO);
            result_list.add(result);
            response.add(latestMessageVO);
        }


        Collections.sort(result_list, (o1, o2) -> {
            LocalDateTime time1 = (LocalDateTime) o1.get("time");
            LocalDateTime time2 = (LocalDateTime) o2.get("time");
            return time2.compareTo(time1);
        });
        Collections.sort(response, (o1, o2) -> {
            LocalDateTime time1 = o1.getLastSendTime();
            LocalDateTime time2 = o2.getLastSendTime();
            return time2.compareTo(time1);
        });
        return Result.ok(response);
    }

    @Override
    public Result insertGroupChat(MessageDto messageDto) {
        String groupId = messageDto.getGroupId();
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(groupId);
        groupMessage.setSenderId(Long.valueOf(messageDto.getSendUser()));
        groupMessage.setContent(messageDto.getMessage());
        groupMessage.setType(1);
        groupMessageService.save(groupMessage);
        return Result.ok();
    }

    @Override
    public Result insertChat(ChatContent chatContent) {
        save(chatContent);
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
