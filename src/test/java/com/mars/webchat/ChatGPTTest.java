package com.mars.webchat;

import com.mars.webchat.service.ChatGPTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatGPTTest {

    @Autowired
    private ChatGPTService chatGPTServiceImpl;

    @Test
    void test() {
//        System.out.println(chatGPTServiceImpl.chat("你是谁"));
    }
}
