package com.robod.flowchat.config;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.MsgMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static Cache<String, Session> sessionCaffeine = Caffeine.newBuilder()
                                            .expireAfter(new Expiry<String, Session>() {
                                                @Override
                                                public long expireAfterCreate(String s, Session session, long currentTime) {
                                                    return TimeUnit.MINUTES.toNanos(10);
                                                }
                                                @Override
                                                public long expireAfterUpdate(String s, Session session, long currentTime, @NonNegative long currentDuration) {
                                                    return currentDuration;
                                                }

                                                @Override
                                                public long expireAfterRead(String s, Session session, long currentTime, @NonNegative long currentDuration) {
                                                    return currentDuration;
                                                }
                                            })
                                            .removalListener((key,session,cause) -> {
                                                if(cause == RemovalCause.EXPIRED){
                                                    // 这个应该关闭session
                                                    try {
                                                        session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Connection idle closed by caffeine"));
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                            })
                                            .build();

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
        sessionCaffeine.put(username, session);
        log.info("WebSocket connection opened for user: {} pool size:{}", session.getRequestParameterMap(), sessionCaffeine.estimatedSize());
        sendMessage(new MsgEntity("系统消息",username, "欢迎来到FlowChat！请开始聊天吧！"));
        redisTemplate.opsForValue().set("onlineUser", sessionCaffeine.estimatedSize());
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message! session:{}, message:{}", session.getRequestParameterMap(), message);

        MsgEntity msgEntity = JSON.parseObject(message, MsgEntity.class);
        // 如果消息内容时 PING 说明是心跳 就不用就行后续的处理
        if (MsgEntity.MsgType.HEART_BEAT.equals(msgEntity.getType())) {
            log.info("================== Received PING message =====================");
            sendMessage(new MsgEntity("system", msgEntity.getSender(), "PONG", MsgEntity.MsgType.HEART_BEAT));
            // 如果时 PING 就刷新一下缓存 相当于续费时间
            sessionCaffeine.put(msgEntity.getSender(), session);
            return;
        }
        // 设置为 ISO 8601 格式的时间戳
        msgEntity.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // 查看消息的接收方 将消息推送给接收方
        sendMessage(msgEntity);
        // 将消息存储到数据库中
        try {
            msgMapper.insert(msgEntity);
            log.info("Message stored in database: {}", msgEntity);
        } catch (Exception e) {
            log.error("Failed to store message in database: {}", e.getMessage());
        }
    }
    @OnClose
    public void onClose(CloseReason closeReason,Session session) throws IOException {
//        String userId = session.getId();
        String username = session.getRequestParameterMap().get("username").get(0);
//        sessionPool.remove(username);
        // 切换 caffeine 之后应该手动移除 session 否则一直等待超时之后才会移除
        sessionCaffeine.invalidate(username);
        session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Connection idle closed by caffeine"));
        log.info("WebSocket connection closed for user: {} pool size:{}", username, sessionCaffeine.estimatedSize());
        redisTemplate.opsForValue().set("onlineUser", sessionCaffeine.estimatedSize());
    }
    @OnError
    public void onError(Throwable throwable) {
        log.error("WebSocket connection error for user: {} pool size:{}", sessionCaffeine.estimatedSize(), throwable.getMessage());
    }
//    String sender,String receiver, String message
    public void sendMessage(MsgEntity message) {

        String sender = message.getSender();
        String receiver = message.getReceiver();
        String messageJsonStr = JSON.toJSONString(message);

        if (!sessionCaffeine.asMap().containsKey(receiver) && !MsgEntity.MsgType.HEART_BEAT.equals(message.getType())) {
            log.warn("No WebSocket session found for user: {}", receiver);
            // 如果用户没有上线 就暂时把消息存储在 Redis 中 左侧进入 右侧取出
            try {
                redisTemplate.opsForList().leftPush(receiver+":unread:"+sender, messageJsonStr);
                log.info("Message stored in Redis for user {}: {}", receiver, messageJsonStr);
            }catch (Exception e) {
                log.error("Failed to store message in Redis for user {}: {}", receiver, e.getMessage());
            }
            return;
        }

        sessionCaffeine.getIfPresent(receiver).getAsyncRemote().sendText(messageJsonStr);
        log.info("{} Sent message to user {}: {}",sender, receiver, messageJsonStr);
    }
}
