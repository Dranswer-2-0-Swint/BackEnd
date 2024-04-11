package com.t3q.dranswer.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t3q.dranswer.common.trace.LogTrace;
import com.t3q.dranswer.entity.LoggingEntity;
import com.t3q.dranswer.common.trace.TraceStatus;
import com.t3q.dranswer.repository.LoggingRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter  {

    private final LoggingRepository loggingRepository;
    private final LogTrace logTrace;

    public LoggingFilter(LoggingRepository loggingRepository, LogTrace logTrace) {
        this.loggingRepository = loggingRepository;
        this.logTrace = logTrace;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //filter 예외 URI
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/api-docs") || requestURI.startsWith("/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }

        TraceStatus status = null;

        try {
            status = logTrace.begin(request.getRequestURI());

            //요청과 응답을 담아 두는 Wrapper
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);


            // 실제 필터 체인 실행
            filterChain.doFilter(requestWrapper, responseWrapper);


            //db에 저장하는 메서드
            saveLog(requestWrapper,responseWrapper);
            //wrapper -> 실제 response
            responseWrapper.copyBodyToResponse();


            logTrace.end(status);

        } catch (Throwable e) {
            //e.printStackTrace();
            //logTrace.exception(status, e);
            throw e;
        }

    }

    private void saveLog(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) throws JsonProcessingException {


        String requestHeaders = getHeaders(requestWrapper).toString();
        String requestURL = requestWrapper.getRequestURI();
        String requestBody = contentBody(requestWrapper.getContentAsByteArray());
        String responseBody = contentBody(responseWrapper.getContentAsByteArray());

        Integer resStatusCode = responseWrapper.getStatus();


        ObjectMapper resobjectMapper = new ObjectMapper();
        JsonNode resNode = resobjectMapper.readTree(responseBody);
        String message = resNode.has("message") ? resNode.get("message").toString() : "No Message";

        log.info("\n|FILTER|\n\trequest URL: {}\n\trequest Header: {}\n\trequest Body: {} \n",requestURL, requestHeaders, requestBody);
        log.info("\n|FILTER|\n\tresponse Statuscode: {}\n\tresponse Body: {} \n", resStatusCode, responseBody);

        LoggingEntity logEntity = new LoggingEntity();
        String requestId ="신창민 로컬"; //responseWrapper.getHeader("request_id");
        logEntity.setReq_id("신창민로컬필터"); //변경
        logEntity.setReq_user("신창민로컬필터");
        logEntity.setReq_prm(requestWrapper.getQueryString());
        logEntity.setReq_dt(LocalDateTime.now());
        logEntity.setReq_uri(requestWrapper.getRequestURI());
        logEntity.setReq_md(requestWrapper.getMethod());

        logEntity.setReq_user("신창민로컬필터");
        logEntity.setReq_body(requestBody);
        logEntity.setRes_user("신창민로컬필터");
        logEntity.setRes_body(responseBody);
        logEntity.setRes_dt(LocalDateTime.now());
        logEntity.setRes_msg(message); // 변경 resNode.get("message").toString())
        logEntity.setRes_status(responseWrapper.getStatus());

        loggingRepository.save(logEntity);
    }
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();

        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private Map<String, String> getQueryParameter(HttpServletRequest request) {
        Map<String, String> queryMap = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> queryMap.put(key, String.join("", value)));
        return queryMap;
    }

    private String contentBody(final byte[] contents) {
        StringBuilder sb = new StringBuilder("\n");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(contents)));
        bufferedReader.lines().forEach(str -> sb.append(str).append("\n"));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
