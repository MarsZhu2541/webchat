package com.mars.webchat.service.impl;

import com.alibaba.dashscope.common.Message;
import com.mars.webchat.util.ChatServiceProxy;
import org.junit.jupiter.api.Test;

class QwenServiceImplTest {

    QwenServiceImpl tongYiService;
    @Test
    void text2TextTest() {

        tongYiService = new QwenServiceImpl("");
        ChatServiceProxy<Message> messageChatServiceProxy = new ChatServiceProxy<>(tongYiService);
        System.out.println(messageChatServiceProxy.chat("你是谁"));
    }

}