package com.mars.webchat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TencentNews {

    List<Id> idlist;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Id {
        List<NewsItem> newslist;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class NewsItem {
        String title;
        String short_url;
        String miniProShareImage;
    }

    public static News toNews(TencentNews tencentNews) {
        return new News(tencentNews.getIdlist().get(0).newslist.stream()
                .map(newsItem -> new News.NewsInfo(newsItem.title, newsItem.short_url,
                        new News.Image(newsItem.miniProShareImage)))
                .collect(Collectors.toList()).subList(1, 11));
    }

}
