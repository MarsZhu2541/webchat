package com.mars.webchat.service.impl;

import com.mars.webchat.model.News;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TouTiaoNewsServiceImplTest {

    @Test
    void getNews() {
        News news = new TouTiaoNewsServiceImpl().getNews();
    }
}
