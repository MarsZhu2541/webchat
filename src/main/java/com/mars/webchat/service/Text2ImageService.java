package com.mars.webchat.service;

import com.mars.webchat.model.ImageMessage;
import net.mamoe.mirai.contact.Group;

public interface Text2ImageService {

    public ImageMessage getImage(Group subject, String keyword);
}
