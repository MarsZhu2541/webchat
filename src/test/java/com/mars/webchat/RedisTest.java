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
        Long length = redisTemplate.opsForList().size("onlineUsers");

        // 逐个删除数组中的元素
        for (int i = 0; i < length; i++) {
            redisTemplate.opsForList().leftPop("onlineUsers");
        }
    }

}
