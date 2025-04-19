package com.kanyu.chat.dto;

import com.kanyu.chat.entity.ChatContent;
import com.kanyu.chat.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ChatContentDto {
    private ChatContent goodsId;
    private User sendUser;
    private User recieiveUser;
    private List<ChatContent> chatContents;
    private Integer notRead;
}
