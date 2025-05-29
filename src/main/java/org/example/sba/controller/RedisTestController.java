package org.example.sba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/redis")
@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set")
    public String setValue() {
        redisTemplate.opsForValue().set("hello", "world");
        return "Set OK";
    }

    @GetMapping("/get")
    public String getValue() {
        return (String) redisTemplate.opsForValue().get("hello");
    }
}
