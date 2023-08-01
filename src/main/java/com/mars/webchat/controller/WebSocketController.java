package com.mars.webchat.controller;

import com.mars.webchat.model.ChatMessage;
import com.mars.webchat.model.MessageType;
import com.mars.webchat.service.MessageService;
import com.mars.webchat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;


@ServerEndpoint("/websocket/{userId}")
@Component
@Slf4j
public class WebSocketController {

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // session集合,存放对应的session
    private static ConcurrentHashMap<Integer, Session> sessionPool = new ConcurrentHashMap<>();

    // concurrent包的线程安全Set,用来存放每个客户端对应的WebSocket对象。
    private static CopyOnWriteArraySet<WebSocketController> webSocketSet = new CopyOnWriteArraySet<>();
    private static com.google.gson.Gson Gson = new Gson();
    private static MessageService messageServiceImpl;
    private static UserService userServiceImpl;
    private static List<String> userList = asList("zwk", "zsj", "zmy", "ymh", "lgh", "zbl");

    @Autowired
    public void webSocketController(MessageService messageServiceImpl, UserService userServiceImpl) {
        WebSocketController.messageServiceImpl = messageServiceImpl;
        WebSocketController.userServiceImpl = userServiceImpl;
    }

    /**
     * 建立WebSocket连接
     *
     * @param session
     * @param userId  用户ID
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") Integer userId) {
        log.info("WebSocket建立连接中,连接用户ID：{}", userId);
        try {
            Session historySession = sessionPool.get(userId);
            // historySession不为空,说明已经有人登陆账号,应该删除登陆的WebSocket对象
            if (historySession != null) {
                userServiceImpl.logOff(userId);
                sendUserOnlineStateMessage(userId, MessageType.LOGOUT);
                webSocketSet.remove(historySession);
                historySession.close();
            }
        } catch (IOException e) {
            log.error("重复登录异常,错误信息：" + e.getMessage(), e);
        }
        // 建立连接
        this.session = session;
        webSocketSet.add(this);
        sessionPool.put(userId, session);
        userServiceImpl.login(userId);
        sendUserOnlineStateMessage(userId, MessageType.LOGIN);
        log.info("建立连接完成,当前在线人数为：{}", webSocketSet.size());
    }

    /**
     * 发生错误
     *
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        Integer userId = valueOf(session.getPathParameters().get("userId"));
        sendUserOnlineStateMessage(userId, MessageType.LOGOUT);
        userServiceImpl.logOff(userId);
        webSocketSet.remove(this);
        log.info("连接断开,当前在线人数为：{}", webSocketSet.size());
    }

    /**
     * 接收客户端消息
     *
     * @param message 接收的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "userId") Integer userId) {
        log.info("收到客户端发来的消息：{}", message);
        ChatMessage chatMessage = new ChatMessage(userId, userList.get(userId), message, MessageType.CHAT);
        sendMessageToOthers(userId, Gson.toJson(chatMessage));
        messageServiceImpl.addMessage(chatMessage);
    }

    /**
     * 推送消息到指定用户
     *
     * @param userId  用户ID
     * @param message 发送的消息
     */
    public void sendMessageByUser(Integer userId, String message) {
        log.info("用户ID：" + userId + ",推送内容：" + message);
        Session session = sessionPool.get(userId);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("推送消息到指定用户发生错误：" + e.getMessage(), e);
        }
    }

    /**
     * 群发消息
     *
     * @param message 发送的消息
     */
    public static void sendAllMessage(String message) {
        log.info("发送消息：{}", message);
        for (WebSocketController webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("群发消息发生错误：" + e.getMessage(), e);
            }
        }
    }

    public void sendMessageToOthers(Integer sender, String message) {
        log.info("发送消息：{}", message);

        Session senderSession = sessionPool.get(sender);
        webSocketSet.stream().filter(webSocket -> !webSocket.session.equals(senderSession))
                .forEach(webSocket -> {
                    try {
                        webSocket.session.getBasicRemote().sendText(message);
                    } catch (IOException| IllegalStateException e) {
                        log.error("Error when sendMessage: ", e);
                    }
                });
    }

    public void sendUserOnlineStateMessage(Integer userId, MessageType type) {
        sendMessageToOthers(userId, Gson.toJson(new ChatMessage(userId, userList.get(userId), "", type)));
    }

}

