package com.mars.webchat.controller;

import com.mars.webchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webchat")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    @GetMapping("/onlineUsers")
    public List<Integer> getOnlineUsers(){
        return userServiceImpl.getOnlineUsers();
    }

}
