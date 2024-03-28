package com.t3q.dranswer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import com.t3q.dranswer.exception.exception.RestApiException;
import com.t3q.dranswer.vo.ResResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static com.t3q.dranswer.config.webclient.WebClientConfig.*;


@Slf4j
public class RetrieveTest {

    private final String HOST = "http://127.0.0.1:8888";
    private WebClient webClient;

    public static final ObjectMapper OM = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        webClient = commonWebClient(defaultExchangeStrategies(), defaultHttpClient(connectionProvider()));
    }

    @Test
    @DisplayName("200")
    public void exchange_NonBlock_200() {
        String URI = HOST + "/test/200";
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        StepVerifier.create(this.retrievePostForMono(URI, requestBody))
                .expectNextMatches(response -> Objects.equals(response.getBody(), ResResult.success("Request Success")))
                .verifyComplete();
    }

    @Test
    public void exchange_NonBlock_400() {
        String URI = HOST + "/test/400";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("test", "body");

        StepVerifier.create(this.retrievePostForMono(URI, requestBody))
                .expectErrorMatches(response ->
                        response instanceof RestApiException && response.getMessage().contains("4xx 외부 요청 오류"))
                .verify();
    }

    @Test
    public void exchange_NonBlock_500() {
        String URI = HOST + "/test/500";
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        StepVerifier.create(this.retrievePostForMono(URI, requestBody))
                .expectErrorMatches(response ->
                        response instanceof WebClientResponseException && response.getMessage().contains("5xx 외부 시스템 오류"))
                .verify();
    }

    @Test
    public void exchange_NonBlock_Timeout() {
        String URI = HOST + "/test/timeout";
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        StepVerifier.create(this.retrievePostForMono(URI, requestBody))
                .expectError(WebClientRequestException.class)
                .verify();
    }

    private Mono<ResponseEntity<ResResult>> retrievePostForMono(String uri, MultiValueMap<String, String> body) throws WebClientResponseException {
        return webClient
                .post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, response ->
//                        Mono.error(
//                                new BadWebClientRequestException(
//                                        response.statusCode().value(),
//                                        String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
//                                                response.statusCode().value(),
//                                                response.bodyToMono(String.class),
//                                                response.headers().asHttpHeaders())
//                                )
//                        )
//                )
//                .onStatus(HttpStatusCode::is5xxServerError, response ->
//                        Mono.error(
//                                new WebClientResponseException(
//                                        response.statusCode().value(),
//                                        String.format("5xx 외부 시스템 오류. %s", response.bodyToMono(String.class)),
//                                        response.headers().asHttpHeaders(), null, null
//                                )
//                        )
//                )
                .toEntity(ResResult.class);
    }
}