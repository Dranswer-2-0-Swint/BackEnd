package com.t3q.dranswer.exception.exception;

import com.t3q.dranswer.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException{

    private final ErrorCode errorCode;

}
