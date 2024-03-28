package com.t3q.dranswer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t3q.dranswer.dto.keycloak.KeycloakTokenIntrospectReq;
import com.t3q.dranswer.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class KeycloakControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KeycloakService keycloakService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Setup mock behavior here (if needed)
    }

    @Test
    public void testPostTokenIntrospect() throws Exception {
        KeycloakTokenIntrospectReq tokenReq = new KeycloakTokenIntrospectReq();
        tokenReq.setToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaQWpEYXZHTDkzZ3Jia3U4Z1ZkUG1tS2N1emRqS1oyNE9CVTRpSWdTU0cwIn0.eyJleHAiOjE3MTE2MTMwNzYsImlhdCI6MTcxMTU5ODY3NiwianRpIjoiM2U1M2Y5MjEtZDY0NC00ZTM5LTllZmMtNWMzODBkMThmYWFhIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLWRldi5kcmFuc3dlci1nLmNvLmtyL3JlYWxtcy9zZXJ2aWNlLXVzZXItZGV2IiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6IjQ0YzkzYWIwLTI4MmEtNGYxZC05MGEyLTU3MGY1MzRkMGE3ZCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlcnZwb3QiLCJzZXNzaW9uX3N0YXRlIjoiYjk5NGU0Y2QtM2JhOC00N2NmLTgyMjItZmNkY2QwNmNiZWFiIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vZGV2LmRyYW5zd2VyLWcuY28ua3I6ODQ0My8iLCIxNzUuNDUuMjIxLjIyNzo1MDAwIiwiaHR0cHM6Ly9kZXYuZHJhbnN3ZXItZy5jby5rciIsIjIyMy4xMzAuMTczLjcyOjgwLyIsImh0dHBzOi8vd3d3LmRyYW5zd2VyLWcuY28ua3IvIiwiaHR0cHM6Ly9kZXZlLmRyYW5zd2VyLWcuY28ua3IiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIkEiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1zZXJ2aWNlLXVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbIm1hbmFnZS11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBncm91cC1zY29wZSBwcm9maWxlIiwic2lkIjoiYjk5NGU0Y2QtM2JhOC00N2NmLTgyMjItZmNkY2QwNmNiZWFiIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBob25lIjoiMDEwNDA0NTk5NTkiLCJvcmdhbml6YXRpb24iOiJ0M3EiLCJuYW1lIjoi7Iug7LC966-8IiwiZ3JvdXBzIjpbIi9sZXZlbC9hIiwiL29yZ2FuaXphdGlvbi9hZG1pbi90M3EiXSwiY3JlYXRlZF9hdCI6IjE3MDI2MTEwNTk3OTQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJjbXNoaW5AdDNxLmNvbSIsInRlYW0iOiJzd2ludCIsImdpdmVuX25hbWUiOiIiLCJmYW1pbHlfbmFtZSI6IuyLoOywveuvvCIsImVtYWlsIjoiY21zaGluQHQzcS5jb20ifQ.HbUJtahqTLR208Q90aQOl_ZQR3wpZ6TytJdQwmWknn8gkshTWoiSUZdeVQZKKYzPNu-VSwt36RXrR-da_7XSUSKGx4CEXg8mF5UOBJuylV_s5tfnOwqc9v9M7XPUE1ANaCiBFHlHB_G4nP1JuAJo0HiYtPAovsHh5lpnZ1l64HZPYq0nDcAwJP-933RFqdsHPNOy34-PsWgHVp6SsCvrJMQXaWaBucobt5SLvbY-V0AExQtL3_fkZSKV3vOP16h13CMgabReNjS45Z0dPmnXfokmT_LQRMLpkHYL2MucrTNtrX1wPabUc-54i-DZE2aJF2Z4z1rBGdU8ReeZ_3_Eqg");

        JsonNode jsonNodeMock = objectMapper.createObjectNode().put("active", true);
        Mono<JsonNode> jsonNodeMono = Mono.just(jsonNodeMock);

        given(keycloakService.postkeycloakIntrospect(tokenReq)).willReturn(jsonNodeMono);


        webTestClient.post().uri("/api/v1/auth/introspectToken")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tokenReq)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(true);
    }

}