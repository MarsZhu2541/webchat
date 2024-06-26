package com.mars.webchat.service.impl;

import com.mars.webchat.service.ChatGPTService;

import java.util.ArrayList;
import java.util.List;

public class ChatServiceProxy<T> {

    private ChatGPTService<T> realService;

    protected final List<T> messages = new ArrayList<>();

    private final int MAX = 12 ;
    private final int TOREMOVE = 4;

    private void messagesSizeCheck(){
        if (messages.size()>=MAX){
            messages.subList(0, TOREMOVE).clear();
        }
    }

    protected void beforeChat(T message) {
        messagesSizeCheck();
        messages.add(message);
    }

    protected void afterChat(T message) {
        messages.add(message);
    }

    public ChatServiceProxy(ChatGPTService<T> realService) {
        this.realService = realService;
    }

    public void setRealService(ChatGPTService<T> realService) {
        this.realService = realService;
    }

    public String chat(String message) {
        beforeChat(createUserMessage(message));
        String answer = realService.chat(messages);
        afterChat(createAssistantMessage(answer));
        return answer;
    }

    public T createUserMessage(String message) {
        return realService.createUserMessage(message);
    }

    public T createAssistantMessage(String message) {
        return realService.createAssistantMessage(message);
    }

}
