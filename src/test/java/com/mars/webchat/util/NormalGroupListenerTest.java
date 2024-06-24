package com.mars.webchat.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalGroupListenerTest {

    @Test
    void text2ImageTest() {
        assertTrue(new NormalGroupListener().isNeedSwitch("切换模式1"));
        assertTrue(new NormalGroupListener().isNeedSwitch("切换模式10086"));
        assertFalse(new NormalGroupListener().isNeedSwitch("切换模式是什么意思"));
        assertFalse(new NormalGroupListener().isNeedSwitch("我要切换模式"));
        assertFalse(new NormalGroupListener().isNeedSwitch("切换模式哈哈"));
    }

}
