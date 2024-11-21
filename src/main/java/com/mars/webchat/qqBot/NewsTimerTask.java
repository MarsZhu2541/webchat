package com.mars.webchat.qqBot;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.model.News;
import com.mars.webchat.service.impl.TencentNewsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.service.RobotService;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NewsTimerTask {

    @Autowired
    private RobotService robotService;

    @Autowired
    private TencentNewsServiceImpl newsService;

    @Value("${itbaima.robot.taskGroup}")
    private long groupNumber;


    @Scheduled(cron = "0 0 7 * * *")
    public void sendDailyNews() {
        log.info("Start cronjob");
        try{
            robotService.sendMessageToGroup(groupNumber, createNewsMessage(newsService.getNews()));
        }catch (RuntimeException e){
            log.error("run cronjob failed", e);
            robotService.sendMessageToGroup(groupNumber, e.getMessage());
        }

    }

    private Message createNewsMessage(News news) {
        List<ImageMessage> messages = news.getData().subList(0,10).stream().map(newsInfo -> {
            try {
                return new ImageMessage(ExternalResource.uploadAsImage(
                        new URL(newsInfo.getImage().getUrl()).openStream(), this.robotService.getGroup(groupNumber)),
                        newsInfo.getTitle() + "\n" + newsInfo.getUrl().split("\\?")[0]);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        return creatMessageChain(messages);
    }

    public static MessageChain creatMessageChain(List<ImageMessage> messages) {

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd号");
        String dateString = sdf.format(today);

        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append("群友们早上好，今天是").append(dateString).append(", 以下是今日早报:\n");
        messages.forEach(imageMessage -> {
            builder.append("\n");
            Optional.ofNullable(imageMessage.getImage()).ifPresent(image -> builder.append(imageMessage.getImage()));
            Optional.ofNullable(imageMessage.getTitle()).ifPresent(title -> builder.append(imageMessage.getTitle()));
            builder.append("\n");
        });
        return builder.build();
    }
}
