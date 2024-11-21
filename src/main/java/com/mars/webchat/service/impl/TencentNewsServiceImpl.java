package com.mars.webchat.service.impl;

import com.google.gson.Gson;
import com.mars.webchat.model.News;
import com.mars.webchat.model.TencentNews;
import com.mars.webchat.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.mars.webchat.model.TencentNews.toNews;

@Service
@Slf4j
public class TencentNewsServiceImpl implements NewsService {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Gson gson = new Gson();


    @Override
    public News getNews() {
        Request request = new Request.Builder()
                .url("https://i.news.qq.com/gw/event/pc_hot_ranking_list?offset=0&page_size=10&appver=15.5_qqnews_7.1.60&rank_id=hot")
                .build();
        try {
            Response response = client.newCall(request).execute();
            News news = toNews(gson.fromJson(response.body().string(), TencentNews.class));
            log.info("got {} pieces of news", news.getData().size());
            return news;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
