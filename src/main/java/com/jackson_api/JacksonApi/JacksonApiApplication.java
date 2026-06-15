package com.jackson_api.JacksonApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JacksonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JacksonApiApplication.class, args);
    }

}
