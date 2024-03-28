package com.t3q.dranswer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.t3q.dranswer.common.annotation.CustomVerifyToken;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenIntrospectReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenReq;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenRes;
import com.t3q.dranswer.exception.errorCode.CommonErrorCode;
import com.t3q.dranswer.exception.exception.RestApiException;
import com.t3q.dranswer.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Keycloak Controller", description = "인증서버 연동 API")
public class KeycloakController {

    @Autowired
    KeycloakService keycloakService;
    private final Logger log = LoggerFactory.getLogger(KeycloakController.class);

    @PostMapping( "/postToken")
    @Operation(
            summary="keycloak login for api",
            description="access_token 과 refresh_token을 가져오는 api"
    )
    @ApiResponse(responseCode = "200", description = "correct info",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = KeycloakTokenRes.class))})
    @ApiResponse(responseCode = "401", description = "incorrect user or client info")
    public Mono<KeycloakTokenRes> postToken(HttpServletRequest request, @RequestBody @Valid KeycloakTokenReq keycloakTokenReq) {
        log.info("getToken API called");
        Mono<KeycloakTokenRes> res = keycloakService.postkeycloakToken(keycloakTokenReq);
        return res;
    }

    @PostMapping("/introspectToken")
    @Operation(
            summary="keycloak token introspect api",
            description="access_token 유효성 검증 및 parsing 한 결과를 return 해주는 api"
    )
    @ApiResponse(responseCode = "200", description = "ok",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "401", description = "incorrect user or client info")
    public Mono<JsonNode> postTokenIntrospect(HttpServletRequest request, @RequestBody KeycloakTokenIntrospectReq token) {
        log.info("introspect api called");
        Mono<JsonNode> res = keycloakService.postkeycloakIntrospect(token);
        return res;
    }


    @CustomVerifyToken
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("\n========================\n=       test called      =\n=========================");
        throw new RestApiException(CommonErrorCode.BAD_REQUEST);
    }




}
