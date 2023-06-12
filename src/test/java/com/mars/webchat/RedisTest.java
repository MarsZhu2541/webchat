package com.mars.webchat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void test() {
//        redisTemplate.opsForValue().set("name", "卷心菜");
    }

}
