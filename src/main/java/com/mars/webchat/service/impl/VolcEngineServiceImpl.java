package com.mars.webchat.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.ChatGPTService;
import com.mars.webchat.service.Text2ImageService;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.plexpt.chatgpt.entity.chat.Message;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.volcengine.service.visual.model.response.VisualHighAesSmartDrawingResponse;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
@Slf4j
public class VolcEngineServiceImpl implements Text2ImageService, ChatGPTService {
    IVisualService visualService = VisualServiceImpl.getInstance();
    ArkService service;
    ChatCompletionRequest chatCompletionRequest;
    final List<ChatMessage> messages = new ArrayList<>();


    public VolcEngineServiceImpl(@Value("${volc.secret_key}") String secretKey, @Value("${volc.access_key}") String accessKey
            , @Value("${volc.ep_id}") String epId) {
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
        service = ArkService.builder().ak(accessKey).sk(secretKey)
                .baseUrl("https://ark.cn-beijing.volces.com/api/v3/").region("cn-beijing").build();
        chatCompletionRequest = ChatCompletionRequest.builder()
                .model(epId)
                .messages(messages)
                .build();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        messages.add(systemMessage);
    }


    @Override
    public ImageMessage getImage(Group subject, String keyword) {
        JSONObject req = new JSONObject();
        req.put("req_key", "high_aes");
        req.put("prompt", keyword);
        req.put("model_version", "general_v1.3");
        try {
            VisualHighAesSmartDrawingResponse response = visualService.visualHighAesSmartDrawing(req);
            byte[] imageBytes = Base64.getDecoder().decode(response.getData().getBinaryDataBase64().get(0));
            return new ImageMessage(ExternalResource.uploadAsImage(new ByteArrayInputStream(imageBytes), subject), "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String chat(List<Message> message) {
        return null;
    }

    @Override
    public String chat(String message) {
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();
        messages.add(userMessage);
        try {
            ChatMessage assistantMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            messages.add(assistantMessage);
            return assistantMessage.stringContent();
        } catch (RuntimeException e) {
            log.error("volc chat failed: ", e);
            return "当前有" + messages.size() + "条上下文\n" + e.getMessage();
        }
    }

    @Override
    public void chatStream(String message) {
//        System.out.println("\n----- streaming request -----");
//        final List<ChatMessage> streamMessages = new ArrayList<>();
//        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
//        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
//        streamMessages.add(streamSystemMessage);
//        streamMessages.add(streamUserMessage);
//        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
//                .model(epId)
//                .messages(streamMessages)
//                .build();
//
//        service.streamChatCompletion(streamChatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(
//                        choice -> {
//                            if (choice.getChoices().size() > 0) {
//                                System.out.print(choice.getChoices().get(0).getMessage().getContent());
//                            }
//                        }
//                );
    }
}
