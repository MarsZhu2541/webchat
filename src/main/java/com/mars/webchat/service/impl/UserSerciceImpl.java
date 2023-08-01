package com.mars.webchat.service.impl;

import com.mars.webchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSerciceImpl implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String ONLINE_USERS = "onlineUsers";

    @Override
    public void login(Integer userId) {
        if (userId != -1 && !getOnlineUsers().contains(userId)) {
            redisTemplate.opsForList().rightPush(ONLINE_USERS, String.valueOf(userId));
        }
    }

    @Override
    public void logOff(Integer userId) {
        if (userId != -1) {
            redisTemplate.opsForList().remove(ONLINE_USERS, 1, String.valueOf(userId));
        }
    }

    @Override
    public List<Integer> getOnlineUsers() {
        return redisTemplate.opsForList().range(ONLINE_USERS, 0, -1).stream()
                .map(Integer::valueOf).collect(Collectors.toList());
    }
}
