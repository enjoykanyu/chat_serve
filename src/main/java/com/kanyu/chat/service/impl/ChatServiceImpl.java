package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.dto.FriendShipDto;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.ChatMapper;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.service.FriendService;
import com.kanyu.chat.service.LoginService;
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
        for (FriendShipDto friend : friends) {
            HashMap<String,Object> result = new HashMap<>(); //内层循环防止覆盖最后消息 bugfix
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
                }else {
                    User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                    result.put("user",actual_user);//存储谁的消息为最后一条
                }
            } else if (cur_cotent.getCreateTime().isBefore(reciver_cotent.getCreateTime())){ //当前用户发送的消息更靠前 则对方的消息更新靠后
                actual_cotent = reciver_cotent;
                unread = query().eq("receive_user_id", user.getId()).eq("send_user_id", friend.getFriendUser().getId()).eq("is_read", 1).count();
                User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                result.put("user",actual_user);//存储谁的消息为最后一条
            }else{
                User actual_user = loginService.query().eq("id", actual_cotent.getSendUserId()).one();
                result.put("user",actual_user);//存储谁的消息为最后一条
            }
            result.put("time",actual_cotent.getCreateTime());//存储消息最后发送时间
            result.put("cotnet",actual_cotent.getMessage());//存储最后一条消息
            result.put("unread",unread);//存储当前用户的未读数量
            result.put("receiver_user",friend.getFriendUser());//拿到当前联系人
            log.info("result"+friend.getFriendUser());
            result_list.add(result);
        }


        Collections.sort(result_list, (o1, o2) -> {
            LocalDateTime time1 = (LocalDateTime) o1.get("time");
            LocalDateTime time2 = (LocalDateTime) o2.get("time");
            return time2.compareTo(time1);
        });
        return Result.ok(result_list);
    }

    @Override
    public Result insertChat(ChatContent chatContent) {
        save(chatContent);
        return Result.ok();
    }
}
