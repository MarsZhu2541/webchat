package com.mars.webchat.controller;

import com.google.gson.Gson;
import com.mars.webchat.service.UserService;

import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/webchat")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    @GetMapping("/onlineUsers")
    public List<Integer> getOnlineUsers() {
        return userServiceImpl.getOnlineUsers();
    }

    @GetMapping("/login")
    public void login(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal =
                (KeycloakPrincipal<KeycloakSecurityContext>) request.getUserPrincipal();
        String tokenString = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
//        response.sendRedirect("http://localhost?token="+tokenString);
        response.sendRedirect("http://124.221.128.48?token="+tokenString);
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        request.logout();
//        response.sendRedirect("http://localhost:8081/webchat/login");
        response.sendRedirect("http://124.221.128.48:8081/webchat/login");
    }
}
