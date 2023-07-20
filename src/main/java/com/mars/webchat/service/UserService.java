package com.mars.webchat.service;

import java.util.List;

public interface UserService {

    public void login(Integer userId);
    public void logOff(Integer userId);
    public List<Integer> getOnlineUsers();

}
