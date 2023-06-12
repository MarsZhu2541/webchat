package com.mars.webchat.controller;

import com.mars.webchat.model.ChatMessage;
import com.mars.webchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webchat")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageServiceImpl;

    @GetMapping("/historyMessages")
    public List<ChatMessage> getAllChatMessages(){
        return messageServiceImpl.getAllMessage();
    }



}
