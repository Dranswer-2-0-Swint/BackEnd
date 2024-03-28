package com.t3q.dranswer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.t3q")
public class DranswerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DranswerApplication.class, args);
    }

}
