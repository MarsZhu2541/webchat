package com.mars.webchat.model;

import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.Image;

@Getter
@Setter
public class ImageMessage {
    Image image;
    String title;

    public ImageMessage(Image image, String title) {
        this.image = image;
        this.title = title;
    }
}
