//package com.mars.webchat;
//
//import com.alibaba.fastjson.JSON;
//import com.mars.webchat.model.ChatMessage;
//import com.mars.webchat.model.MessageType;
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//
//import org.elasticsearch.common.xcontent.XContentType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class RestHighLevelClientTest {
//
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;
//
//    @Test
//    void contextLoads() throws IOException {
//        //创建索引的请求
//        CreateIndexRequest request = new CreateIndexRequest("chat_message");
//        //执行请求
//        CreateIndexResponse createIndexRequest = client.indices().create(request, RequestOptions.DEFAULT);
//
//        System.out.println(createIndexRequest);
//    }
//
//    @Test
//    void testAddDocument() throws IOException {
//
//        ChatMessage msg = new ChatMessage(1,"zwk","hello world", MessageType.CHAT);
//
//        IndexRequest  addRequest = new IndexRequest("chat_message");
//        //ES中的_id设为1
//        addRequest.id("1");
//        addRequest.timeout("1s");
//
//        //将数据放入请求
//        addRequest.source(JSON.toJSONString(msg), XContentType.JSON);
//        //客户端发送请求
//        IndexResponse indexResponse = client.index(addRequest,RequestOptions.DEFAULT);
//        System.out.println(indexResponse.toString());
//        System.out.println(indexResponse.status());
//    }
//
//}
