package com.t3q.dranswer.dto.keycloak;


import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "발급받은 acceess_token")
public class KeycloakTokenIntrospectReq {


    private String token;

}
