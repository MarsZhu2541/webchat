package com.mars.webchat.service.impl;

import com.mars.webchat.model.ImageMessage;
import com.mars.webchat.service.BaiduImageService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BaiduImageServiceImpl implements BaiduImageService {

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Random rand = new Random();

    @Override
    public ImageMessage getImage(Group subject, String keyword) {
        int randomNumber = rand.nextInt(100);
        String url = "https://image.baidu.com/search/acjson?tn=resultjson_com&logid=8289063633285016890&ipn=rj&ct=202426592&fp=result&cl=2&lm=-1&ie=utf-8&oe=utf-8&st=-1&z=&ic=0&" +
                "word=" + keyword + "&pn="+randomNumber+"&rn=1&gsm=1e&1635054081427=";
        try {
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", "BAIDUID=ACD114E40869F28B8C41DCE14079125F:FG=1; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BIDUPSID=ACD114E40869F28B5AF3C1EB8C79E20B")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Connection","keep-alive")
                    .header("Host","image.baidu.com")
                    .addHeader("Accept-Language", "en-US,en;q=0.5")
                    .addHeader("Referer", "https://www.baidu.com")
                    .addHeader("Cache-Control", "max-age=0")
                    .build()).execute();
            String body = response.body().string();
            String reg = "ObjURL\":\\s*\"([^\"]+)";
            String regTitle = "fromPageTitleEnc\":\"([^\"]+)";
            Pattern pattern = Pattern.compile(reg);
            Pattern patternTitle = Pattern.compile(regTitle);
            Matcher m = pattern.matcher(body);
            Matcher mTittle = patternTitle.matcher(body);
            if (m.find() && mTittle.find()) {
                URL imageUrl = new URL(m.group(1).replace("\\",""));
                return new ImageMessage(ExternalResource.uploadAsImage(imageUrl.openStream(), subject), mTittle.group(1));
            }
            throw new RuntimeException("No image found");
        } catch (IOException e) {
            log.error("Found image failed: ", e);
            throw new RuntimeException(e);
        }
    }
}
