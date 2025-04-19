package com.kanyu.user_service.dto;

import com.kanyu.user_service.entity.ChatContent;
import com.kanyu.user_service.entity.User;
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
