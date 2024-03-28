package com.t3q.dranswer.controller;

import com.t3q.dranswer.exception.errorCode.CommonErrorCode;
import com.t3q.dranswer.exception.exception.RestApiException;
import com.t3q.dranswer.vo.ResResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Logger log = LoggerFactory.getLogger(TestController.class);


    @RequestMapping(value = "/200")
    public ResponseEntity<ResResult> res200() {
        log.info("Request res 200");
        return ResponseEntity.ok(ResResult.success("Request Success"));
    }

    @RequestMapping(value = "/400")
    public ResponseEntity<ResResult> res400() {
        log.info("Request res400");
        //return new ResponseEntity<>(ResResult.success("Bad Request"), HttpStatus.BAD_REQUEST);
        throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
    }

    @RequestMapping(value = "/500")
    public ResponseEntity<ResResult> res500() {
        log.info("Request res500");
        throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(value = "/timeout")
    public ResResult timeout() throws InterruptedException {
        log.info("timeout test");
        Thread.sleep(10_000);
        return ResResult.success("timeout test");
    }

}