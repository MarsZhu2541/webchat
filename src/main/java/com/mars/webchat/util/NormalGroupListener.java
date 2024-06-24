package com.mars.webchat.util;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.impl.BaiduImageServiceImpl;
import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import com.mars.webchat.service.impl.VolcEngineServiceImpl;
import com.mars.webchat.service.impl.RandomImageServiceImpl;
import com.plexpt.chatgpt.exception.ChatException;
import lombok.Synchronized;
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

    @Autowired
    private VolcEngineServiceImpl volcEngineService;

    private int functionTag = 0;

    Group group;
    MessageChain messageChain;

    private List<String> modeList = List.of("默认模式","ChatGPT对话", "豆包对话", "豆包文生图", "百度搜图", "随机小猫图片");


    private static List<Message> messages = new ArrayList<>();
    private String intro = """
            你好，我是AI聊天机器人，目前支持功能有:
            1.ChatGPT对话，2.豆包对话，3.豆包文生图，4.百度搜图，5.随机小猫图片。
            您可以@我发送"切换模式+序号"来切换到对应功能。 例如"切换模式1",
            发送"当前模式",可以查看当前模式。
            """;

    @RobotListenerHandler
    public void handleMessage(GroupMessageEvent event) {
        String message = event.getMessage().contentToString().replace("@" + qqNumber, "");

        if (!(event.getMessage().get(1) instanceof At)) {
            return;
        }
        if (((At) event.getMessage().get(1)).getTarget() != qqNumber) {
            return;
        }
        log.info("Received group message: {}", message);
        setUpChat(event.getSubject(), event.getMessage());
        invokeFunctionOnDemand(message);
    }

    @RobotListenerHandler
    public void handleSelfMessage(GroupMessageSyncEvent event) {
        String message = event.getMessage().contentToString().replace("@" + qqNumber, "");
        if (message.contains("tc")) {
            message = message.replace("tc", "");
            log.info("Received self message: {}", message);
            setUpChat(event.getSubject(), event.getMessage());
            invokeFunctionOnDemand(message);
        }
    }

    private void setUpChat(Group group, MessageChain messageChain) {
        this.group = group;
        this.messageChain = messageChain;
    }

    @Synchronized
    private void invokeFunctionOnDemand(String message) {
        try {
            if (isNeedCurrentMode(message)) {
                sendOnlyMessage("当前模式为: " + modeList.get(functionTag));
                return;
            }
            if (isNeedSwitch(message)) {
                functionTag = getTargetFunctionId(message);
                sendOnlyMessage("已切换至模式: " + modeList.get(functionTag));
                return;
            }
            if (isNeedIntro(message)) {
                sendOnlyMessage(intro);
                return;
            }
            switch (functionTag) {
                case 1:
                    sendChatGPTMessage(message);
                    break;
                case 2:
                    sendOnlyMessage(volcEngineService.chat(message));
                    break;
                case 3:
                    log.info("Need Volc Image");
                    sendImage(volcEngineService.getImage(group, message).getImage());
                    break;
                case 4:
                    log.info("Need Baidu Image");
                    sendImageWithMessage(baiduImageService.getImage(group, message));
                    break;
                case 5:
                    sendImage(randomImageService.getImage(group));
                    break;
                case 0:
                default:
                    sendOnlyMessage(intro);
                    break;
            }

        } catch (NumberFormatException e) {
            sendOnlyMessage("输入有误，请重新尝试");
        } catch (RuntimeException e) {
            group.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append(e.getMessage())
                    .build());
        }
    }

    private synchronized void sendChatGPTMessage(String message) {
        try {
            send(message);
        } catch (ChatException e) {
            group.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("出错了，憋急，等我再试一把！\n")
                    .append(e.getMessage())
                    .build());
            messages.clear();
            send(message);
        } catch (Exception e) {
            log.error("Error when send message: ", e);
            group.sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(messageChain))
                    .append("出错了，请联系管理员qq2541884980\n")
                    .append(e.getMessage())
                    .build());
        }
    }

    private void send(String message) {
        addMessage(message, Message.Role.USER);
        String chat = chatGPTService.chat(messages);
        log.info("Sent group message: {}", chat);
        addMessage(chat, Message.Role.ASSISTANT);
        sendOnlyMessage(chat);
    }

    private void sendOnlyMessage(String message) {
        group.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append(message)
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

    private boolean isNeedIntro(String msg) {
        return msg.contains("功能介绍");
    }

    private boolean isNeedSwitch(String msg) {
        return msg.contains("切换模式");
    }

    private boolean isNeedCurrentMode(String msg) {
        return msg.contains("当前模式");
    }

    private int getTargetFunctionId(String msg) {
        return Integer.parseInt(msg.replace("切换模式", "").trim());
    }


    private void sendImage(Image image) {
        log.info("Sent group image message: {}", image.getImageId());
        group.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append(image)
                .build());
    }

    private void sendImageWithMessage(ImageMessage imageMessage) {
        log.info("Sent group image message: {}", imageMessage.getTitle());
        group.sendMessage(new MessageChainBuilder()
                .append(new QuoteReply(messageChain))
                .append(imageMessage.getImage())
                .append(imageMessage.getTitle())
                .build());
    }

}
