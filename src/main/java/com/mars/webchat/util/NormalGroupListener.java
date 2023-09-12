package com.mars.webchat.util;

import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.event.RobotListener;
import net.itbaima.robot.event.RobotListenerHandler;
import net.itbaima.robot.listener.MessageListener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.plexpt.chatgpt.entity.chat.Message;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RobotListener
public class NormalGroupListener extends MessageListener {

    @Value("${itbaima.robot.username}")
    private long qqNumber;

    @Autowired
    private ChatGPTServiceImpl chatGPTServiceImpl;

    private static List<Message> messages = new ArrayList<>();

    @RobotListenerHandler
    public void handleMessage(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();

        if (!(event.getMessage().get(1) instanceof At)) {
            return;
        }
        if (((At) event.getMessage().get(1)).getTarget() != qqNumber) {
            return;
        }
        log.info("Received group message: {}", message);
        try {
            addMessage(message, Message.Role.USER);
            String chat = chatGPTServiceImpl.chat(messages);
            log.info("Sent group message: {}", chat);
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(chat)
                    .build());
        } catch (Exception e) {
            log.error("Error when send message: ", e);
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append("出错了，请联系管理员qq2541884980")
                    .build());
        }
    }

    public static synchronized void addMessage(String chat, Message.Role role){
        if(role == Message.Role.USER){
            messages.add(Message.of(chat));
        }else {
            messages.add(Message.ofAssistant(chat));
        }
        if(messages.size()>=12){
            messages.remove(0);
            messages.remove(0);
        }
    }
}
