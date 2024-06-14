package com.mars.webchat.service.impl;

import org.junit.jupiter.api.Test;

class HighAesSmartDrawingServiceTest {
    @Test
    void test(){
        new HighAesSmartDrawingServiceImpl("","").getImage(null,"在空调房里玩探案笔记的年轻人");
    }

}
