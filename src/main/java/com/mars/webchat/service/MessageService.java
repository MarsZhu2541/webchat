package com.mars.webchat.service;

import com.mars.webchat.model.ChatMessage;

import java.util.List;

public interface MessageService {
    public long addMessage(ChatMessage message);
    public List<ChatMessage> getAllMessage();
}
