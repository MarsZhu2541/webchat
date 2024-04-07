package com.mars.webchat;

import com.mars.webchat.service.ChatGPTService;
import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import com.plexpt.chatgpt.entity.chat.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatGPTTest {


    private ChatGPTServiceImpl chatGPTServiceImpl = new ChatGPTServiceImpl();

    @Test
    void test() {
        System.out.println(chatGPTServiceImpl.chat(List.of(Message.of("你是chatgpt3.5吗"))));
    }
}
