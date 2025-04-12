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
@TableName("group_message")
public class GroupMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 消息唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联群id
     */
    private String groupId;

    /**
     * 发送消息者id
     */
    private Long senderId;


    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息内容类型 0 普通成员消息 1 系统消息 如成员进群 新建群聊
     */
    private Integer type;

   /* *//**
     * 是否被对方已读 0未读 1 已读
     *//*
    private int isRead;*/

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
