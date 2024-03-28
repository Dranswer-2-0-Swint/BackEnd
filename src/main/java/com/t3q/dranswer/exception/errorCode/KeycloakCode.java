package com.t3q.dranswer.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KeycloakCode implements ErrorCode {
    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
