package com.mars.webchat.service.impl;

import com.mars.webchat.service.ChatGPTService;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.Images;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    @Value("${openai.secret_key}")
    private List<String> token;

//    @Value("${proxy.ip}")
//    private String proxyIp;
//    @Value("${proxy.port}")
//    private Integer proxyPort;
    @Override
    public synchronized String chat(List<Message> message) {
        //国内需要代理


        ChatGPT chatGPT = ChatGPT.builder()
//                .proxy(Proxys.http(proxyIp, proxyPort))
                .timeout(900)
                .apiHost("https://hk.xty.app") //反向代理地址
                .build()
                .init();

        Images build = Images.builder().build();

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO_0613.getName())
                .messages(message)
                .maxTokens(3000)
                .temperature(0.9)
                .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public void chatStream(String message) {

        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(600)
                .apiKeyList(token)
//                .proxy(Proxys.http(proxyIp, proxyPort))
                .apiHost("https://api.openai-forward.com/")
                .build()
                .init();

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO_0613.getName())
                .messages(Arrays.asList(Message.of(message)))
                .build();
    }
}
