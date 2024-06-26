package com.mars.webchat.service.impl;
import com.plexpt.chatgpt.entity.chat.Message;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import org.junit.jupiter.api.Test;


class VolcEngineServiceImplTest {

    private String secretKey = "==";
    private String accessKey = "";
    private String epId = "";

    VolcEngineServiceImpl volcEngineService = new VolcEngineServiceImpl(secretKey, accessKey, epId);
    ChatServiceProxy<ChatMessage> chatMessageChatServiceProxy = new ChatServiceProxy<>(volcEngineService);
    @Test
    void text2ImageTest() {
        volcEngineService.getImage(null, "在空调房里玩探案笔记的年轻人");
    }

    @Test
    void text2TextTest() {
        System.out.println(chatMessageChatServiceProxy.chat("你是谁"));
    }

}
