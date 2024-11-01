package com.mars.webchat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class News {

     List<NewsInfo> data;


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NewsInfo {

        String Title;
        String Url;
        Image Image;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Image {
        String url;
    }

}
