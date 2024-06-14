package com.mars.webchat.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.Text2ImageService;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.volcengine.service.visual.model.response.VisualHighAesSmartDrawingResponse;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Base64;


@Service
public class HighAesSmartDrawingServiceImpl implements Text2ImageService {
    IVisualService visualService = VisualServiceImpl.getInstance();

    private String secretKey;
    private String accessKey;


    public HighAesSmartDrawingServiceImpl(@Value("${volc.secret_key}") String secretKey, @Value("${volc.access_key}") String accessKey) {
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        visualService.setAccessKey(accessKey);
        visualService.setSecretKey(secretKey);
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
            return new ImageMessage(ExternalResource.uploadAsImage( new ByteArrayInputStream(imageBytes),subject),"");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
