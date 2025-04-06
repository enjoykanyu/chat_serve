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
@TableName("friendship")
public class Friendship implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 消息唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    //请求加好友的人
    private User requester;

    //接受加好友的人
    private User receiver;

    //加好友的状态 0 请求发送中 1通过 2 拒绝 3拉黑
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
