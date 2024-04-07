package com.mars.webchat.util;

import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import com.plexpt.chatgpt.exception.ChatException;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.event.RobotListener;
import net.itbaima.robot.event.RobotListenerHandler;
import net.itbaima.robot.listener.MessageListener;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageSyncEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
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
        String message = event.getMessage().contentToString().replace("@"+qqNumber, "");

        if (!(event.getMessage().get(1) instanceof At)) {
            return;
        }
        if (((At) event.getMessage().get(1)).getTarget() != qqNumber) {
            return;
        }
        log.info("Received group message: {}", message);
        sendMsg(event, message);
    }

    private void sendMsg(GroupMessageEvent event, String message) {
        sendMessage(message, event.getSubject(), event.getMessage());
    }

    @RobotListenerHandler
    public void handleSelfMessage(GroupMessageSyncEvent event) {
        String message = event.getMessage().contentToString().replace("@"+qqNumber, "");

        if(message.contains("tc")){
            message = message.replace("tc","");
            log.info("Received self message: {}", message);
            sendMessage(message, event.getSubject(), event.getMessage());
        }
    }

    private synchronized void sendMessage(String message, Group subject, MessageChain message2) {
        try {
            send(message, subject, message2);
        } catch (ChatException e) {
            subject.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(message2))
                    .append("出错了，憋急，等我再试一把！\n")
                    .append(e.getMessage())
                    .build());
            messages.clear();
            send(message, subject, message2);
        } catch (Exception e) {
            log.error("Error when send message: ", e);
            subject.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(message2))
                    .append("出错了，请联系管理员qq2541884980\n")
                    .append(e.getMessage())
                    .build());
        }
    }

    private void send(String message, Group subject, MessageChain message2) {
        addMessage(message, Message.Role.USER);
        String chat = chatGPTServiceImpl.chat(messages);
        log.info("Sent group message: {}", chat);
        addMessage(chat, Message.Role.ASSISTANT);
        subject.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(message2))
                .append(chat)
                .build());
    }


    public static synchronized void addMessage(String chat, Message.Role role) {
        if (role == Message.Role.USER) {
            messages.add(Message.of(chat));
        } else {
            messages.add(Message.ofAssistant(chat));
        }
        if (messages.size() >= 12) {
            messages.remove(0);
            messages.remove(0);
            messages.remove(0);
            messages.remove(0);
        }
    }


}
