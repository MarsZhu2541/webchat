package com.mars.webchat.service.impl;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BaiduImageServiceImplTest {
    @Test
    public void getImageTest() throws IOException {
        new BaiduImageServiceImpl().getImage(null,"牛吃草");

//        Response response = new OkHttpClient().newCall(new Request.Builder()
//                .url("https://image.baidu.com/search/acjson?tn=resultjson_com&logid=5179920884740494226&ipn=rj&ct=201326592&fp=result&cl=2&lm=-1&ie=utf-8&oe=utf-8&st=-1&z=&ic=0&copyright=&word=牛吃草&pn=0&rn=1&gsm=1e&1635054081427=")
//                .addHeader("Cookie", "BAIDUID=B95B5BD3594D80B810624FB9DA28C11B:FG=1; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[pAp0hkmvqKD]=mk3SLVN4HKm; BIDUPSID=B95B5BD3594D80B810624FB9DA28C11B; H_WISE_SIDS=60288")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0")
//                .build()).execute();
//        System.out.println(response.body().string());
    }

}
