package com.robod.flowchat.service;

import com.alibaba.fastjson2.JSON;
import com.robod.flowchat.config.WebSocketServer;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.FriendMapper;
import com.robod.flowchat.vo.FriendListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FriendService {
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    public List<FriendListVO> getFriendListAndLastMsg(String username) {
        List<FriendListVO> friendListAndLastMsg = friendMapper.getFriendList(username);
        friendListAndLastMsg
                        .forEach(item -> {
                            // 设置在线状态
                            item.setOnline(WebSocketServer.sessionCaffeine.asMap().containsKey(item.getUsername()));
                            // 从redis中获取一下未读消息数
                            try {
                                String key = item.getUsername() + ":unread:" + item.getFriendName();
                                Long size = redisTemplate.opsForList().size(key);
                                item.setUnreadCount(size == null ? 0 : size.intValue());
                                if(size != null && size.intValue() > 0) {
                                    MsgEntity msgEntity = JSON.parseObject((String) redisTemplate.opsForList().index(key, 0), MsgEntity.class);
                                    item.setLastMessage(msgEntity.getContent());
                                }
                            }catch (Exception e){
                                log.error("Error retrieving unread messages for user {} and friend {}: {}", item.getUsername(), item.getFriendName(), e.getMessage());
                            }
                        });
        log.info("friendList={}", friendListAndLastMsg);
        return friendListAndLastMsg;
    }
}
