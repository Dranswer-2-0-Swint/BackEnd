package com.t3q.dranswer.service.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenIntrospectReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenRes;

import com.t3q.dranswer.service.service.KeycloakService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final WebClient webClient;

    @Value("${env.userClient}")
    private String clientId;

    @Value("${env.userSecret}")
    private String clientSecret;

    @Value("${env.authUrl}")
    private String host;

    @Value("${env.userRealm}")
    private String userRealm;

    private String tokenTypeHint = "access_token";

    private String grantType = "password";


    public KeycloakServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://auth-dev.dranswer-g.co.kr").build();
    }


    @Override
    public Mono<KeycloakTokenRes> postkeycloakToken(KeycloakTokenReq keycloakTokenReq){

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakTokenReq.getClient_id());
        body.add("client_secret", keycloakTokenReq.getClient_secret());
        body.add("password", keycloakTokenReq.getPassword());
        body.add("grant_type", keycloakTokenReq.getGrant_type());
        body.add("username", keycloakTokenReq.getUsername());

        return webClient.post()
                .uri("/realms/service-user-dev/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(KeycloakTokenRes.class);

    }


    @Override
    public Mono<JsonNode> postkeycloakIntrospect(KeycloakTokenIntrospectReq token) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("client_id", this.clientId );
        body.add("client_secret", this.clientSecret );
        body.add("token_type_hint", tokenTypeHint);
        body.add("token", token.getToken());

        return webClient.post()
                .uri("/realms/service-user-dev/protocol/openid-connect/token/introspect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

}
