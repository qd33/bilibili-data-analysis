package com.qd33.bilibili_analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 设置超时时间
        factory.setConnectTimeout(10000); // 10秒连接超时
        factory.setReadTimeout(30000);    // 30秒读取超时

        // 如果需要代理，可以取消注释以下代码
        /*
        factory.setProxy(new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress("your-proxy-host", your-proxy-port)));
        */

        return new RestTemplate(factory);
    }
}