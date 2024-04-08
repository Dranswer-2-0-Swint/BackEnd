package com.t3q.dranswer.common.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.t3q.dranswer.exception.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class TokenVerificationAspect {

    @Autowired
    private final RestClient keycloakClient;

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

    public TokenVerificationAspect(@Qualifier("keycloakClient") RestClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }


    @Around("@annotation(com.t3q.dranswer.common.annotation.CustomVerifyToken)")
    public Object verifyToken(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("access_token");

        if (token == null || !isTokenValid(token)) {
            throw new InvalidTokenException("Invalid or missing token");
        }

        return joinPoint.proceed();
    }

    private boolean isTokenValid(String token) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId );
        body.add("client_secret", this.clientSecret );
        body.add("token_type_hint", tokenTypeHint);
        body.add("token", token);

        ResponseEntity<JsonNode> introspectClaim = keycloakClient.post()
                .uri("/realms/service-user-dev/protocol/openid-connect/token/introspect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(JsonNode.class);

        // "active" 필드 값 가져오기. 필드가 없다면 false 반환
        Boolean isActive = introspectClaim.getBody().get("active").asBoolean();
        return isActive;

    }
}