package com.t3q.dranswer.dto.keycloak;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "keycloak 사용자 정보")
@AllArgsConstructor
public class KeycloakTokenReq {

    @NotBlank(message = "클라이언트 이름을 입력해주세요.")
    @Size(min = 6,max = 100)
    @Schema(description = "클라이어트 이름", example = "xxx-svc-1")
    private String client_id;

    @NotBlank(message = "클라이언트 시크릿을 입력해주세요.")
    @Size(min = 32,max = 32)
    @Schema(description = "클라이어트 시크릿", example = "ciIsImF6cCI6InN...")
    private String client_secret;


    @Schema(description = "사용자이름", example = "xxxx@gmail.com")
    private String username;


    @Schema(description = "비밀번호는 최소 8글자 이상", example = "")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "클라이언트 이름을 입력해주세요.")
    @Schema(description = "클라이어트 이름", example = "xxx-svc-1")
    private String grant_type;


}
