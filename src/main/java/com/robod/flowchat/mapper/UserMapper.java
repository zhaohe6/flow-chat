package com.robod.flowchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robod.flowchat.entity.UserEntity;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<UserEntity> {
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    UserEntity findByUsername(String username); // 根据用户名查询用户实体
}
