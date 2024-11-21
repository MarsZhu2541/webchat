package com.mars.webchat.service.impl;

import com.mars.webchat.model.News;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TencentNewsServiceImplTest {
    @Test
    void test(){
        News news = new TencentNewsServiceImpl().getNews();
        news.getData().forEach(newsInfo -> {
            System.out.println(newsInfo.getTitle());
        });
    }

}
