package com.mars.webchat.service.impl;

import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import org.junit.jupiter.api.Test;

import java.util.List;

class ZhiPuServiceImplTest {

    @Test
    void chat() {
        System.out.println(new ZhiPuServiceImpl("")
                .chat(List.of(new ChatMessage(ChatMessageRole.USER.value(),"你是谁"))));
    }

    @Test
    void textToVideo() {
        new ZhiPuServiceImpl("")
                .getImage(null, "牛吃草");
    }
}
