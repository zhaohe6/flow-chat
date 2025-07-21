package com.robod.flowchat.service;

import com.alibaba.fastjson2.JSON;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.MsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

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
            Long totalSize = redisTemplate.opsForList().size(key);
            List<MsgEntity> redisMsg = (List<MsgEntity>) redisTemplate.opsForList().rightPop(key, totalSize)
                    .stream().map(msg -> com.alibaba.fastjson2.JSON.parseObject((String) msg, MsgEntity.class))
                    .collect(Collectors.toList());
            if(redisMsg != null && !redisMsg.isEmpty()) {
                // 将redis中的未读消息添加到查询结果中
                friendMessage.addAll(redisMsg);
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
