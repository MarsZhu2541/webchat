package com.mars.webchat.service.impl;


import com.mars.webchat.service.ChatGPTService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HunyuanServiceImpl implements ChatGPTService<Message> {

    private String secretId;
    private String secretKey;

    public HunyuanServiceImpl(@Value("${hunyuan.secret_id}") String apiKey, @Value("${hunyuan.secret_key}") String secretKey) {
        this.secretId = apiKey;
        this.secretKey = secretKey;
    }
    @Override
    public String chat(List<Message> messages) {
        ChatCompletionsResponse resp = null;
        try{
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            HunyuanClient client = new HunyuanClient(cred, "", clientProfile);
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            req.setModel("hunyuan-pro");
            req.setMessages(messages.toArray(Message[]::new));
            resp = client.ChatCompletions(req);
            return resp.getChoices()[0].getMessage().getContent();
        } catch (TencentCloudSDKException e) {
            return e.getMessage();
        } catch (NullPointerException e){
            return resp.getErrorMsg().getMsg();
        }
    }

    @Override
    public void chatStream(String message) {
    }

    @Override
    public Message createUserMessage(String message) {
        Message msg = new Message();
        msg.setRole("user");
        msg.setContent(message);
        return msg;
    }

    @Override
    public Message createAssistantMessage(String message) {
        Message msg = new Message();
        msg.setRole("assistant");
        msg.setContent(message);
        return msg;
    }
}
