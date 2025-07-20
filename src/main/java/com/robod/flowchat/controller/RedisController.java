package com.robod.flowchat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RedisController {
    @Autowired
    private RedisTemplate redisTemplate;
    public static final String REDIS_KEY = "flowchat:messages";
    @GetMapping("/test/redis")
    public String getRedisKey() {
        redisTemplate.opsForValue().set(REDIS_KEY, "Redis key initialized");
        return REDIS_KEY;
    }
}
