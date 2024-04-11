package com.t3q.dranswer.service;

import com.t3q.dranswer.common.ApplicationProperties;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenReq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


@Slf4j
@Service
public class KeycloakService {

    private final RestClient keycloakClient;

    private final ApplicationProperties applicationProperties;

    private String tokenTypeHint = "access_token";
    private String grantType = "password";

    public KeycloakService(@Qualifier("keycloakClient") RestClient keycloakClient, ApplicationProperties applicationProperties) {
        this.keycloakClient = keycloakClient;
        this.applicationProperties = applicationProperties;
    }


    public ResponseEntity<String> postkeycloakToken(KeycloakTokenReq keycloakTokenReq){
        String tokenURI = applicationProperties.getUserTokenUrl();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakTokenReq.getClient_id());
        body.add("client_secret", keycloakTokenReq.getClient_secret());
        body.add("password", keycloakTokenReq.getPassword());
        body.add("grant_type", keycloakTokenReq.getGrant_type());
        body.add("username", keycloakTokenReq.getUsername());

        ResponseEntity<String> result = keycloakClient.post()
                .uri(tokenURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve().toEntity(String.class);

        return result;
    }

//    public Mono<JsonNode> postkeycloakIntrospect(KeycloakTokenIntrospectReq token) {
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//
//        body.add("client_id", this.clientId );
//        body.add("client_secret", this.clientSecret );
//        body.add("token_type_hint", tokenTypeHint);
//        body.add("token", token.getToken());
//
//        return webClient.post()
//                .uri("/realms/service-user-dev/protocol/openid-connect/token/introspect")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData(body))
//                .retrieve()
//                .bodyToMono(JsonNode.class);
//    }





}
