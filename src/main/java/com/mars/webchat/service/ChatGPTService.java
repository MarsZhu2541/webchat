package com.mars.webchat.service;

import com.plexpt.chatgpt.entity.chat.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatGPTService {

    public String chat(List<Message> message);

    public void chatStream(String message);
}
