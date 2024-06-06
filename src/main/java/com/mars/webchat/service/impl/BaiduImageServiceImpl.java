package com.mars.webchat.service.impl;

import com.mars.webchat.service.BaiduImageService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BaiduImageServiceImpl implements BaiduImageService {

    OkHttpClient client = new OkHttpClient().newBuilder().build();

    @Override
    public Image getImage(Group subject, String keyword) {

        String url = "https://image.baidu.com/search/acjson?tn=resultjson_com&logid=5179920884740494226&ipn=rj&ct=201326592&fp=result&cl=2&lm=-1&ie=utf-8&oe=utf-8&st=-1&z=&ic=0&" +
                "word=" + keyword + "&pn=0&rn=1&gsm=1e";
        try {
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", "BAIDUID=B95B5BD3594D80B810624FB9DA28C11B:FG=1; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[pAp0hkmvqKD]=mk3SLVN4HKm; BIDUPSID=B95B5BD3594D80B810624FB9DA28C11B; H_WISE_SIDS=60288")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0")
                    .build()).execute();
            String reg = "ObjURL\":\\s*\"([^\"]+)";
            Pattern pattern = Pattern.compile(reg);
            Matcher m = pattern.matcher(response.body().string());
            if (m.find()) {
                URL imageUrl = new URL(m.group(1).replace("\\",""));
                return ExternalResource.uploadAsImage(imageUrl.openStream(), subject);
            }
            throw new RuntimeException("No image found");
        } catch (IOException e) {
            log.error("Found image failed: ", e);
            throw new RuntimeException(e);
        }
    }
}
