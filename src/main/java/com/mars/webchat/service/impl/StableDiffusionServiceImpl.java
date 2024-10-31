package com.mars.webchat.service.impl;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.Text2ImageService;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StableDiffusionServiceImpl implements Text2ImageService {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    @Override
    public ImageMessage getImage(Group subject, String keyword) {
        Request request = new Request.Builder()
                .url(String.format("https://sweet-sun-31de.2541884980.workers.dev/?p=%s&s=20", keyword))
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new ImageMessage(ExternalResource.uploadAsImage(response.body().byteStream(), subject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
