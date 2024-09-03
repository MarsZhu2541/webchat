package com.mars.webchat.service.impl;

import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HunyuanServiceImplTest {
    HunyuanServiceImpl hunyuanService = new HunyuanServiceImpl("","");
    @Test
    void chat() {
        System.out.println( hunyuanService.chat(List.of(hunyuanService.createUserMessage("你是谁？"))));
    }
}
