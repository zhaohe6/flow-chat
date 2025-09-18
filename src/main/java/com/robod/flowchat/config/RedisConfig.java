package com.robod.flowchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 统一Key的序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用JSON序列化Object值
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 自定义Long序列化器
        RedisSerializer<Long> longSerializer = new RedisSerializer<Long>() {
            private final Charset charset = StandardCharsets.UTF_8;

            @Override
            public byte[] serialize(Long value) {
                return value == null ? null : value.toString().getBytes(charset);
            }

            @Override
            public Long deserialize(byte[] bytes) {
                if (bytes == null || bytes.length == 0) return null;
                try {
                    return Long.parseLong(new String(bytes, charset));
                } catch (NumberFormatException e) {
                    throw new SerializationException("Cannot deserialize", e);
                }
            }
        };

        // 设置所有值的序列化器
        template.setValueSerializer(longSerializer);
        template.setHashValueSerializer(longSerializer);
//        template.setDefaultSerializer(longSerializer); // 关键！设置默认序列化

        template.afterPropertiesSet();
        return template;
    }
}
