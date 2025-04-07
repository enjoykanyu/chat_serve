package com.kanyu.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("friend_request") //好友申请实体类
public class FriendRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 消息唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 发起好友申请的人
     */
    private Long requesterId;

    /**
     * 接受好友申请的人
     */
    private User receiverId;

    /**
     * 好友申请验证理由
     */
    private String reason;

    /**
     * 好友申请状态 0 申请中 1已通过 2已拒绝
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}