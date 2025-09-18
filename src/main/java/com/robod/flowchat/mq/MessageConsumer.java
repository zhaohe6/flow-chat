package com.robod.flowchat.mq;

import com.robod.flowchat.entity.MsgEntity;
import com.robod.flowchat.mapper.MsgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {
    @Autowired
    private MsgMapper  msgMapper;
    @RabbitListener(queues = "chat01.chatMsgSaveMySQLQueue")
    public void receiveMessageToMySQL(MsgEntity msgEntity){
        System.out.println("MySQL 消息队列接收到消息: " + msgEntity);
        try {
            msgMapper.insert(msgEntity);
            log.info("Message stored in database: {}", msgEntity);
        } catch (Exception e) {
            log.error("Failed to store message in database: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "chat01.chatMsgSaveRedisQueue")
    public void receiveMessageToRedis(String message){
        System.out.println("Redis消息队列接收到消息: " + message);
    }

}
