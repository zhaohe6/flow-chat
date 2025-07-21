package com.robod.flowchat.config;

import com.alibaba.fastjson2.JSON;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.MsgMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {

    private static RedisTemplate redisTemplate;
    private static MsgMapper msgMapper;
    public static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        WebSocketServer.redisTemplate = redisTemplate;
    }
    @Autowired
    public void setMsgMapper(MsgMapper msgMapper) {
        WebSocketServer.msgMapper = msgMapper;
    }
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // 添加发送者的 id

        String username = session.getRequestParameterMap().get("username").get(0);
        sessionPool.put(username, session);
        log.info("WebSocket connection opened for user: {} pool size:{}", session.getRequestParameterMap(), sessionPool.size());
        sendMessage("系统消息",username, "欢迎来到FlowChat！请开始聊天吧！");
        // 查询redis中当前用户是否有未读消息
//        Long size = redisTemplate.opsForList().size(username + ":unread");
//        while (size > 0) {
//            // 如果有未读消息 就将未读消息取出
//            String unreadMessage = (String) redisTemplate.opsForList().rightPop(username + ":unread", 0, TimeUnit.SECONDS);
//            if (unreadMessage != null) {
//                session.getAsyncRemote().sendText(unreadMessage);
//                log.info("Sent unread message to user {}: {}", username, unreadMessage);
//            }
//            size--;
//        }

    }
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message! session:{}, message:{}", session.getRequestParameterMap(), message);
        MsgEntity msgEntity = JSON.parseObject(message, MsgEntity.class);
        msgEntity.setTimestamp(java.time.LocalDateTime.now());
        // 查看消息的接收方 将消息推送给接收方
        sendMessage(msgEntity.getSender(), msgEntity.getReceiver(), msgEntity.getContent());
        // 将消息存储到数据库中
        try {
            msgMapper.insert(msgEntity);
            log.info("Message stored in database: {}", msgEntity);
        } catch (Exception e) {
            log.error("Failed to store message in database: {}", e.getMessage());
        }
    }
    @OnClose
    public void onClose(CloseReason closeReason,Session session) {
        String userId = session.getId();
        sessionPool.remove(userId);
        log.info("WebSocket connection closed for user: {} pool size:{}", userId, sessionPool.size());
    }
    @OnError
    public void onError(Throwable throwable) {
        log.error("WebSocket connection error for user: {} pool size:{}", sessionPool.size(), throwable.getMessage());
    }
    public void sendMessage(String sender,String receiver, String message) {

        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setSender(sender);
        msgEntity.setReceiver(receiver);
        msgEntity.setContent(message);
        message = JSON.toJSONString(msgEntity);

        if (!sessionPool.containsKey(receiver)) {
            log.warn("No WebSocket session found for user: {}", receiver);
            // 如果用户没有上线 就暂时把消息存储在 Redis 中 左侧进入 右侧取出
            try {
//                redisTemplate.opsForHash().put("user:messages", receiver, message);
                redisTemplate.opsForList().leftPush(receiver+":unread:"+sender, message);
                log.info("Message stored in Redis for user {}: {}", receiver, message);
            }catch (Exception e) {
                log.error("Failed to store message in Redis for user {}: {}", receiver, e.getMessage());
            }
            return;
        }

        sessionPool.get(receiver).getAsyncRemote().sendText(message);
        log.info("{} Sent message to user {}: {}",sender, receiver, message);
    }
}
