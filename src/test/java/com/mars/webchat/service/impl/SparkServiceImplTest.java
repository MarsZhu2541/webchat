package com.mars.webchat.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SparkServiceImplTest {

    @Test
    void text2TextTest() {
        SparkServiceImpl sparkService = new SparkServiceImpl("", "");
        ChatServiceProxy<SparkServiceImpl.Text> textChatServiceProxy = new ChatServiceProxy<>(sparkService);
        System.out.println(textChatServiceProxy.chat("你是谁"));
    }
}
