package com.mars.webchat.qqBot;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.model.News;
import com.mars.webchat.service.impl.*;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import net.itbaima.robot.event.RobotListener;
import net.itbaima.robot.event.RobotListenerHandler;
import net.itbaima.robot.listener.MessageListener;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageSyncEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.plexpt.chatgpt.entity.chat.Message;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mars.webchat.qqBot.NewsTimerTask.creatMessageChain;


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

    @Autowired
    private SparkServiceImpl sparkService;

    @Autowired
    private HunyuanServiceImpl hunyuanService;

    @Autowired
    private StableDiffusionServiceImpl stableDiffusionService;

    @Autowired
    private ZhiPuServiceImpl zhiPuService;

    @Autowired
    private TouTiaoNewsServiceImpl touTiaoNewsService;

    @Autowired
    private TencentNewsServiceImpl tencentNewsService;

    private ChatServiceProxy<Message> chatgptServiceProxy;
    private ChatServiceProxy<ChatMessage> volcChatServiceProxy;
    private ChatServiceProxy<SparkServiceImpl.Text> sparkChatServiceProxy;
    private ChatServiceProxy<com.tencentcloudapi.hunyuan.v20230901.models.Message> hunyuanChatServiceProxy;
    private ChatServiceProxy<com.zhipu.oapi.service.v4.model.ChatMessage> zhiPuServiceProxy;

    private List<ChatServiceProxy> chatProxyList;

    private int functionTag = 0;

    private final List<String> modeList = List.of("默认模式", "ChatGPT对话", "豆包对话", "讯飞星火对话", "混元对话",
            "智谱对话", "豆包文生图", "智谱文生视频", "Stable Diffusion", "百度搜图", "随机小猫图片", "头条新闻", "腾讯新闻");
    private final String intro = """
            你好，我是AI聊天机器人，目前支持功能有:
            1.ChatGPT对话，2.豆包对话，3.讯飞星火对话，4.混元对话，5.智谱对话，6.豆包文生图，7.智谱文生视频，8.Stable Diffusion，9.百度搜图，10.随机小猫图片，10.头条新闻，12.腾讯新闻。
            您可以@我发送"切换模式+序号"来切换到对应功能。 例如"切换模式1",
            发送"当前模式",可以查看当前模式。
            """;

    private final ThreadLocal<Group> group = new ThreadLocal<>();
    private final ThreadLocal<MessageChain> messageChain = new ThreadLocal<>();


    @PostConstruct
    public void setUp() {
        chatgptServiceProxy = new ChatServiceProxy<>(chatGPTService);
        volcChatServiceProxy = new ChatServiceProxy<>(volcEngineService);
        sparkChatServiceProxy = new ChatServiceProxy<>(sparkService);
        hunyuanChatServiceProxy = new ChatServiceProxy<>(hunyuanService);
        zhiPuServiceProxy = new ChatServiceProxy<>(zhiPuService);
        chatProxyList = List.of(chatgptServiceProxy, volcChatServiceProxy, sparkChatServiceProxy,
                hunyuanChatServiceProxy, zhiPuServiceProxy);
    }

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
        invokeFunctionOnDemand(message, event.getSubject(), event.getMessage());
    }

    @RobotListenerHandler
    public void handleSelfMessage(GroupMessageSyncEvent event) {
        String message = event.getMessage().contentToString().replace("@" + qqNumber, "");
        if (message.contains("tc")) {
            message = message.replace("tc", "");
            log.info("Received self message: {}", message);
            invokeFunctionOnDemand(message, event.getSubject(), event.getMessage());
        }
    }

    private void invokeFunctionOnDemand(String message, Group group, MessageChain messageChain) {
        setUpEvent(group, messageChain);
        try {
            if (isNeedIntro(message)) {
                sendImageMessage(intro);
                return;
            }

            if (isNeedCurrentMode(message)) {
                sendImageMessage("当前模式为: " + modeList.get(functionTag));
                return;
            }

            if (isNeedSwitch(message)) {
                sendSwitchModeMessage(message);
                if (functionTag == 11) {
                    sendNewsMessage(touTiaoNewsService.getNews(), group);
                } else if (functionTag == 12) {
                    sendNewsMessage(tencentNewsService.getNews(), group);
                }
                return;
            }
            switch (functionTag) {
                case 1, 2, 3, 4, 5:
                    sendImageMessage(chatProxyList.get(functionTag - 1).chat(message));
                    break;
                case 6:
                    log.info("Need Volc Image");
                    sendImageMessage(volcEngineService.getImage(group, message).getImage());
                    break;
                case 7:
                    log.info("Need zhipu video");
                    sendImageMessage(zhiPuService.getImage(group, message));
                    break;
                case 8:
                    log.info("Need Stable Diffusion Image");
                    sendImageMessage(stableDiffusionService.getImage(group, message));
                    break;
                case 9:
                    log.info("Need Baidu Image");
                    sendImageMessage(baiduImageService.getImage(group, message));
                    break;
                case 10:
                    sendImageMessage(randomImageService.getImage(group));
                    break;
                case 11:
                    sendNewsMessage(touTiaoNewsService.getNews(), group);
                    break;
                case 12:
                    sendNewsMessage(tencentNewsService.getNews(), group);
                    break;
                case 0:
                default:
                    sendImageMessage(intro);
                    break;
            }

        } catch (
                NumberFormatException e) {
            sendImageMessage("输入有误，请重新尝试");
        } catch (
                RuntimeException e) {
            log.error("Error when send message: ", e);
            sendImageMessage("出错了，请联系管理员qq2541884980\n" + e.getMessage());
        } finally {
            this.group.remove();
            this.messageChain.remove();
        }
    }

    private void sendNewsMessage(News news, Contact contact) {
        List<ImageMessage> messages = news.getData().subList(0, 10).stream().map(newsInfo -> {
            try {
                return new ImageMessage(ExternalResource.uploadAsImage(new URL(newsInfo.getImage().getUrl()).openStream(), contact),
                        newsInfo.getTitle() + "\n" + newsInfo.getUrl().split("\\?")[0]);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        group.get().sendMessage(creatMessageChain(messages));
    }


    private void setUpEvent(Group group, MessageChain messageChain) {
        this.group.set(group);
        this.messageChain.set(messageChain);
    }


    private void sendSwitchModeMessage(String message) {
        int previousTag = functionTag;
        functionTag = getTargetFunctionId(message);
        try {
            sendImageMessage("已切换至模式: " + modeList.get(functionTag));
        } catch (IndexOutOfBoundsException e) {
            functionTag = previousTag;
            sendImageMessage("输入有误，请重新尝试");
        }
    }

    private boolean isNeedIntro(String msg) {
        return "功能介绍".equals(msg.trim());
    }

    public boolean isNeedSwitch(String msg) {
        return Pattern.compile("切换模式-?\\d+").matcher(msg).find();
    }

    private boolean isNeedCurrentMode(String msg) {
        return ("当前模式").equals(msg.trim());
    }

    private int getTargetFunctionId(String msg) {
        Matcher matcher = Pattern.compile("切换模式-?(\\d+)").matcher(msg);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return Integer.parseInt(msg.replace("切换模式", "").trim());
    }

    private void sendImageMessage(Image image) {
        ImageMessage imageMessage = new ImageMessage(image);
        this.sendImageMessage(imageMessage);
    }

    private void sendImageMessage(String message) {
        ImageMessage imageMessage = new ImageMessage(message);
        this.sendImageMessage(imageMessage);
    }

    private void sendImageMessage(ImageMessage imageMessage) {
        log.info("Sent group image message: {}", imageMessage.getTitle());
        MessageChainBuilder builder = new MessageChainBuilder().append(new QuoteReply(messageChain.get()));
        Optional.ofNullable(imageMessage.getImage()).ifPresent(image -> builder.append(imageMessage.getImage()));
        Optional.ofNullable(imageMessage.getTitle()).ifPresent(title -> builder.append(imageMessage.getTitle()));
        group.get().sendMessage(builder.build());
    }
}
