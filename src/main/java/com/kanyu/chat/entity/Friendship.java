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
@TableName("friendship") //好友关系实体类
public class Friendship implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 消息唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    //当前用户id
    private Long userId;

    //当前用户的好友id
    private Long friendId;

    //加好友的状态 0处理中 1好友 2 当前用户删除类对方 3拉黑
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
