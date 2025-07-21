package com.robod.flowchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.robod.flowchat.entity.MsgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface MsgMapper extends BaseMapper<MsgEntity> {
    // 根据发送人和接收人查询最新的十条消息
    @Select("select * from t_message where sender = #{username} and receiver = #{friendName} order by timestamp desc limit 10;")
    public List<MsgEntity> getFriendMessage(@Param("username") String username, @Param("friendName") String friendName);
}
