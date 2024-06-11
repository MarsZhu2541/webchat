package com.mars.webchat.service;

import com.mars.webchat.model.ImageMessage;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;

public interface BaiduImageService {

    public ImageMessage getImage(Group subject, String keyword);
}
