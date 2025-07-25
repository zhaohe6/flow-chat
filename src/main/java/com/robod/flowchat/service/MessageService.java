package com.robod.flowchat.service;

import com.alibaba.fastjson2.JSON;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.MsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class MessageService {
    @Autowired
    private MsgMapper msgMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    public List<MsgEntity> getFriendMessage(String username, String friendName) {
        List<MsgEntity> friendMessage = msgMapper.getFriendMessage(username, friendName);
        try{
            String key = username + ":unread:" + friendName;
            // 从redis中查询这个key是否存在
            if(!redisTemplate.hasKey(key)) {
                // 如果不存在，则直接返回查询结果
                return friendMessage;
            }

            List<String> msgs = redisTemplate.opsForList().range(key, 0, -1);
            List<MsgEntity> redisMsg = msgs.stream()
                    .map(msg -> JSON.parseObject(msg, MsgEntity.class))
                    .collect(Collectors.toList());
            redisTemplate.delete(key);
            if(redisMsg != null && !redisMsg.isEmpty()) {
                // 将redis中的未读消息添加到查询结果中
                friendMessage.addAll(redisMsg);
                // 去掉 消息id 重复的消息
                friendMessage = friendMessage.stream()
                        .collect(Collectors.toMap(MsgEntity::getId, msg -> msg, (msg1, msg2) -> msg1))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
                // 按时间戳降序排序
                friendMessage.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));
            }
        }catch (Exception e){
            // 如果redis中没有未读消息，或者发生异常，则不处理
            e.printStackTrace();
        }


        return friendMessage;
    }
}
