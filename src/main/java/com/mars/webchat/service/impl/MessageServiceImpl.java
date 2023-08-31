package com.mars.webchat.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.mars.webchat.model.ChatMessage;
import com.mars.webchat.model.MessageType;
import com.mars.webchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private StringRedisTemplate redisTemplate;
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;

    private static com.google.gson.Gson Gson = new Gson();

    @Override
    public long addMessage(ChatMessage message) {
//        IndexRequest addRequest = new IndexRequest("chat_message");
//        addRequest.timeout("1s");
//        //将数据放入请求
//        addRequest.source(JSON.toJSONString(message), XContentType.JSON);
//        //客户端发送请求
//        client.index(addRequest, RequestOptions.DEFAULT);
        return redisTemplate.opsForList().rightPush("chatMessages", Gson.toJson(message));
    }

    @Override
    public List<ChatMessage> getAllMessage() {
        List<String> chatMessages = redisTemplate.opsForList().range("chatMessages", 0, -1);
        return chatMessages.stream().map(msgStr -> Gson.fromJson(msgStr, ChatMessage.class)).collect(Collectors.toList());
    }
}
