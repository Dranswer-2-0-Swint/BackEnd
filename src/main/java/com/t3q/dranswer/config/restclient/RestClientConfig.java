package com.t3q.dranswer.config.restclient;

import com.t3q.dranswer.interceptor.LoggingInterceptor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;


@Configuration
public class RestClientConfig {
    @Autowired
    private final LoggingInterceptor loggingInterceptor;

    @Bean
    ClientHttpRequestFactory clientHttpRequestFactory() {
        // Timeout 설정을 위한 RequestConfig 인스턴스 생성
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(300000, TimeUnit.MILLISECONDS)		// 연결 timeout: 5분
                .setResponseTimeout(300000, TimeUnit.MILLISECONDS)	// 읽기 timeout: 5분
                .build();

        // Custom configuration을 적용한 HttpClient 생성
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
    private String host = "https://auth-dev.dranswer-g.co.kr";

    public RestClientConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Bean("keycloakClient")
    public RestClient keycloakClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        return RestClient.builder()
                .baseUrl(host)
                .requestInterceptor(loggingInterceptor)
                .requestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory))
                .build();
    }

}
