package com.gx;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GxblogApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient clint;
    @Test
    void contextLoads() throws IOException {
        CreateIndexRequest request= new CreateIndexRequest("blogs");
      CreateIndexResponse response= clint.indices().create(request, RequestOptions.DEFAULT);
      System.out.println(response);
    }
    @Test
    void testExistIndex() throws IOException {
        //1.创建索引的请求
        GetIndexRequest request = new GetIndexRequest("blogs");
        //2客户端执行请求，请求后获得响应
        boolean exist =  clint.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("测试索引是否存在-----"+exist);
    }

}
