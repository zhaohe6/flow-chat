package com.robod.flowchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME_REDIS = "chat01.chatMsgSaveRedisQueue";
    public static final String QUEUE_NAME_MySQL = "chat01.chatMsgSaveMySQLQueue";
    public static final String EXCHANGE_NAME = "chat01.topicExchange";
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    @Bean
    public Queue chatMsgSaveMySQLQueue() {// 创建队列
        return new Queue(QUEUE_NAME_MySQL);
    }
    @Bean
    public Queue chatMsgSaveRedisQueue() {//创建队列
        return new Queue(QUEUE_NAME_REDIS);
    }
    @Bean
    public Binding bindChatMsgSaveMySQLQueue(TopicExchange topicExchange, Queue chatMsgSaveMySQLQueue) {
        return BindingBuilder.bind(chatMsgSaveMySQLQueue)
                .to(topicExchange)
                .with("chat01.*");
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
