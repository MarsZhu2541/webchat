package com.mars.webchat.controller;

import com.mars.webchat.model.ChatMessage;
import com.mars.webchat.service.MessageService;
import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/webchat")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageServiceImpl;

    @Autowired
    private ChatGPTServiceImpl chatGPTServiceImpl;

    @GetMapping("/historyMessages")
    public List<ChatMessage> getAllChatMessages(){
        return messageServiceImpl.getAllMessage();
    }

    @GetMapping("/chat/sse")
    public SseEmitter sseEmitter(Integer userId, String prompt) {
        return chatGPTServiceImpl.chatStream(userId, prompt.replace("@ChatGPT ",""));
    }

}
