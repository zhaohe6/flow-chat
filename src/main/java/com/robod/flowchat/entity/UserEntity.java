package com.robod.flowchat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user") // 假设用户表名为 t_user
public class UserEntity {
    /**
     * 主键（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private Date createdAt;
}
