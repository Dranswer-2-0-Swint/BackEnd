package com.t3q.dranswer.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenIntrospectReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenRes;
import reactor.core.publisher.Mono;

public interface KeycloakService {
    Mono<KeycloakTokenRes> postkeycloakToken (KeycloakTokenReq keycloakTokenReq);

    Mono<JsonNode> postkeycloakIntrospect(KeycloakTokenIntrospectReq token);
}
