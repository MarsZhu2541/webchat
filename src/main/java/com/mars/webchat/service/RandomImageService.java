package com.mars.webchat.service;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;

public interface RandomImageService {
    public Image getImage(Group subject);
}
