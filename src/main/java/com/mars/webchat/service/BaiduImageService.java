package com.mars.webchat.service;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;

public interface BaiduImageService {

    public Image getImage(Group subject, String keyword);
}
