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
@TableName("group_member")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联群id
     */
    private String groupId;

    /**
     * 群成员id关联user表
     */
    private Long userId;

    /**
     * 群成员状态 1 存在 2已退群
     */
    private Integer status;

    /**
     * 进群时间
     */
    private LocalDateTime join_time;

    /**
     * 群成员角色 0 平台成员 1 管理员 2 群主
     */
    private Integer role;
}
