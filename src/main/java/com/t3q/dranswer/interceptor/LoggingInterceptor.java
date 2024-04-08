package com.t3q.dranswer.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t3q.dranswer.entity.LoggingEntity;
import com.t3q.dranswer.repository.LoggingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Log4j2
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private final LoggingRepository loggingRepository;

    public LoggingInterceptor(LoggingRepository loggingRepository) {
        this.loggingRepository = loggingRepository;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        saveLog(request, body, response);

        return response;
    }

    private void saveLog(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        String requestBody = new String(body, StandardCharsets.UTF_8);
        String responseBody = getResponseBody(response);

        ObjectMapper resobjectMapper = new ObjectMapper();
        JsonNode resNode = resobjectMapper.readTree(responseBody);
        String message = resNode.has("message") ? resNode.get("message").toString() : "No Message";

        log.info("\n|Interceptor|\n\trequest URL: {}\n\trequest Header: {}\n\trequest Body: {} \n",request.getURI(), request.getHeaders(), requestBody);
        log.info("\n|Interceptor|\n\tresponse Statuscode: {}\n\tresponse Body: {} \n", response.getStatusCode(), responseBody);
        //요청을 Entity에 저장
        LoggingEntity logEntity = new LoggingEntity();
        logEntity.setReq_id("신창민로컬인터셉터"); //request.getHeaders().getFirst("request_id")
        logEntity.setReq_user("신창민로컬인터셉터");
        logEntity.setReq_body(requestBody);
        logEntity.setReq_dt(LocalDateTime.now());
        logEntity.setReq_uri(String.valueOf(request.getURI()));
        logEntity.setReq_md(String.valueOf(request.getMethod()));
        logEntity.setReq_prm(request.getURI().getQuery());

        //응답 저장
        logEntity.setRes_user(response.getHeaders().getOrigin());
        logEntity.setRes_body(responseBody);
        logEntity.setRes_dt(LocalDateTime.now());
        logEntity.setRes_msg(message);
        logEntity.setRes_status(response.getStatusCode().value());

        loggingRepository.save(logEntity);

    }
    private String getResponseBody(ClientHttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()));
        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        return responseBody.toString();
    }
}