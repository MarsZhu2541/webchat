package com.mars.webchat.service.impl;

import com.google.gson.Gson;
import com.mars.webchat.model.ChatMessage;
import com.mars.webchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static com.google.gson.Gson Gson = new Gson();

    @Override
    public long addMessage(ChatMessage message) {
        return redisTemplate.opsForList().rightPush("chatMessages", Gson.toJson(message));
    }

    @Override
    public List<ChatMessage> getAllMessage() {
        List<String> chatMessages = redisTemplate.opsForList().range("chatMessages", 0, -1);
        return chatMessages.stream().map(msgStr -> Gson.fromJson(msgStr, ChatMessage.class)).collect(Collectors.toList());
    }
}
