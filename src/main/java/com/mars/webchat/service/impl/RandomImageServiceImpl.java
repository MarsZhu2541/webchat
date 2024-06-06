package com.mars.webchat.service.impl;

import com.mars.webchat.service.RandomImageService;
import jakarta.annotation.Resource;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RandomImageServiceImpl implements RandomImageService {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    @Override
    public Image getImage(Group subject) {
        Request request = new Request.Builder()
                .url("https://loremflickr.com/1024/1024")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return ExternalResource.uploadAsImage(response.body().byteStream(),subject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
