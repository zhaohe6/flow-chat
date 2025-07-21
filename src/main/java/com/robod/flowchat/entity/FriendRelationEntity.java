package com.robod.flowchat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_friend_relation")
public class FriendRelationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("friend")
    private String friend;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;
}
