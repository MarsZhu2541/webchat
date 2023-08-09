package com.mars.webchat.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatGPTService {
    public String chat(String message);
    public void chatStream(String message);
}
