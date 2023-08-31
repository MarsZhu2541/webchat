package com.mars.webchat.util;

import com.mars.webchat.service.impl.ChatGPTServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.event.RobotListener;
import net.itbaima.robot.event.RobotListenerHandler;
import net.itbaima.robot.listener.MessageListener;
import net.itbaima.robot.service.RobotService;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@RobotListener
public class NormalGroupListener extends MessageListener {

    @Value("${itbaima.robot.username}")
    private long qqNumber;

    @Autowired
    private ChatGPTServiceImpl chatGPTServiceImpl;

    @Autowired
    private RobotService robotService;

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
            String chat = chatGPTServiceImpl.chat(message);
            log.info("Sent group message: {}", chat);
            robotService.sendMessageToGroup(event.getGroup().getId(), chat);
        } catch (Exception e) {
            log.error("Error when send message: ");
            robotService.sendMessageToGroup(event.getGroup().getId(), "出错了，请联系管理员qq2541884980");
        }
    }
}
