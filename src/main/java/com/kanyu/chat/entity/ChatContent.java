package com.kanyu.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_db")
public class ChatContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 消息唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息者id
     */
    private Long sendUserId;

    /**
     * 接收消息者id
     */
    private Long receiveUserId;


    /**
     * 消息内容
     */
    private String message;

    /**
     * 是否被对方已读 0未读 1 已读
     */
    private int isRead;

    /**
     * 机器人sessionId
     */
    private String sessionId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;



}
