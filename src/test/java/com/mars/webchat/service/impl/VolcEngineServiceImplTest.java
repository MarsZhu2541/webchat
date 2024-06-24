package com.mars.webchat.service.impl;
import org.junit.jupiter.api.Test;


class VolcEngineServiceImplTest {

    private String secretKey = "==";
    private String accessKey = "";
    private String epId = "";

    VolcEngineServiceImpl volcEngineService = new VolcEngineServiceImpl(secretKey, accessKey, epId);
    @Test
    void text2ImageTest() {
        volcEngineService.getImage(null, "在空调房里玩探案笔记的年轻人");
    }

    @Test
    void text2TextTest() {
        System.out.println(volcEngineService.chat("你是谁"));
    }

}
