package com.kanyu.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kanyu.chat.common.Result;
import com.kanyu.chat.dto.ChatContentDto;
import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import com.kanyu.chat.mapper.ChatMapper;
import com.kanyu.chat.service.ChatService;
import com.kanyu.chat.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, ChatContent> implements ChatService {

    @Resource
    LoginService loginService;
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

            ArrayList<ChatContent> allContent = new ArrayList<>();
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
}
