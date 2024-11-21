package com.mars.webchat;

import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import com.mars.webchat.util.ChatServiceProxy;
import com.plexpt.chatgpt.entity.chat.Message;
import org.junit.jupiter.api.Test;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatGPTTest {


    private ChatGPTServiceImpl chatGPTServiceImpl = new ChatGPTServiceImpl();
    private ChatServiceProxy<Message> serviceProxy = new ChatServiceProxy<>(chatGPTServiceImpl);

    @Test
    void test() {
        System.out.println(serviceProxy.chat("你是chatgpt3.5吗"));
    }
}
