package com.mars.webchat.service.impl;

import com.mars.webchat.util.ChatServiceProxy;
import org.junit.jupiter.api.Test;

class SparkServiceImplTest {

    @Test
    void text2TextTest() {
        SparkServiceImpl sparkService = new SparkServiceImpl("", "");
        ChatServiceProxy<SparkServiceImpl.Text> textChatServiceProxy = new ChatServiceProxy<>(sparkService);
        System.out.println(textChatServiceProxy.chat("你是谁"));
    }
}
