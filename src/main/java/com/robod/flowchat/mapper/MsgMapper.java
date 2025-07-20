package com.robod.flowchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.robod.flowchat.entity.MsgEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgMapper extends BaseMapper<MsgEntity> {
    // 这里可以添加自定义的查询方法
    // 例如：List<MsgEntity> findBySender(String sender);
}
