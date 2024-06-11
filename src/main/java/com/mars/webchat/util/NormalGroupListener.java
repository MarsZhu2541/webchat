package com.mars.webchat.util;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.impl.BaiduImageServiceImpl;
import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import com.mars.webchat.service.impl.RandomImageServiceImpl;
import com.plexpt.chatgpt.exception.ChatException;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.event.RobotListener;
import net.itbaima.robot.event.RobotListenerHandler;
import net.itbaima.robot.listener.MessageListener;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageSyncEvent;
import net.mamoe.mirai.message.data.*;
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
    private ChatGPTServiceImpl chatGPTService;

    @Autowired
    private RandomImageServiceImpl randomImageService;


    @Autowired
    private BaiduImageServiceImpl baiduImageService;

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

        invokeFunctionOnDemand(message, event.getSubject(), event.getMessage());
    }

    @RobotListenerHandler
    public void handleSelfMessage(GroupMessageSyncEvent event) {
        String message = event.getMessage().contentToString().replace("@"+qqNumber, "");
        if(message.contains("tc")){
            message = message.replace("tc","");
            log.info("Received self message: {}", message);
            invokeFunctionOnDemand(message, event.getSubject(), event.getMessage());
        }
    }

    private synchronized void sendChatGPTMessage(String message, Group subject, MessageChain message2) {
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
        String chat = chatGPTService.chat(messages);
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

    private boolean isNeedCatImage(String msg){
        return msg.contains("猫");
    }

    private void sendImage(Group subject, MessageChain chain, Image image){
        log.info("Sent group image message: {}", image.getImageId());
        subject.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(chain))
                .append(image)
                .build());
    }

    private void invokeFunctionOnDemand(String message, Group subject, MessageChain messageQuote){
        try {
            if(isNeedCatImage(message)){
                sendImage(subject, messageQuote, randomImageService.getImage(subject));
                return;
            }
            if(isNeedBaiduImage(message)){
                log.info("Need Baidu Image");
                ImageMessage imageMessage = baiduImageService.getImage(subject, message.replace("搜图", ""));
                sendImageWithMessage(subject, messageQuote, imageMessage);
                return;
            }
            sendChatGPTMessage(message, subject, messageQuote);
        }catch (RuntimeException e){
            subject.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(messageQuote))
                    .append(e.getMessage())
                    .build());
        }
    }

    private void sendImageWithMessage(Group subject, MessageChain messageQuote, ImageMessage imageMessage) {
        log.info("Sent group image message: {}", imageMessage.getTitle());
        subject.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(messageQuote))
                .append(imageMessage.getImage())
                .append(imageMessage.getTitle())
                .build());
    }

    private boolean isNeedBaiduImage(String message) {
        return message.contains("搜图");
    }
}
