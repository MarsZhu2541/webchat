package com.mars.webchat.service.impl;

import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import org.junit.jupiter.api.Test;

import java.util.List;

class ZhiPuServiceImplTest {

    @Test
    void chat() {
        System.out.println(new ZhiPuServiceImpl("cc70547902db6bf247f55b6bb0cd4956.IZ0KxVIqc4eAmXRS")
                .chat(List.of(new ChatMessage(ChatMessageRole.USER.value(),"写个故事，武汉小米上班的24岁程序员男生周末不陪女朋友还想找兼职找出路担心自己年纪大了被裁，想重生回中考时，但初三还是太难了，他问上帝有没有小学的，上帝直接教他玩赛尔号，最后智力退化，只能被小米的在职员工现场亲身教学，学习幼儿编程，历经99八十一难，重走编程路，但是走到中途就只想赚点钱之后每天挂壁混吃等死，遂决定开一家鹅尔玛的超市，在开超市的途中偶遇超市king，遂整合吃完了的零售行业，这时回想起袁嵩的酒店管理系统，利用公交线路的最短路径将超市开遍全武汉，导致武汉人心惶惶，害怕失业于是考公去了"))));
    }

    @Test
    void textToVideo() {
        new ZhiPuServiceImpl("")
                .getImage(null, "牛吃草");
    }
}
