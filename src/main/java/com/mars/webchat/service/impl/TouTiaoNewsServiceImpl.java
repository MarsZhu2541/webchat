package com.mars.webchat.service.impl;

import com.google.gson.Gson;
import com.mars.webchat.model.News;
import com.mars.webchat.service.NewsService;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TouTiaoNewsServiceImpl implements NewsService {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Gson gson = new Gson();


    @Override
    public News getNews() {
        Request request = new Request.Builder()
                .url("https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return gson.fromJson(response.body().string(), News.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
