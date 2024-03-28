package com.t3q.dranswer.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeycloakTokenRes {
    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private String expires_in;

    @JsonProperty("refresh_expires_in")
    private String refresh_expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;
}
