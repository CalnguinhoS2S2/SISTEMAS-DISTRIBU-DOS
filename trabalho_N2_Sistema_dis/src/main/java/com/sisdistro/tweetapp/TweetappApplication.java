package com.sisdistro.tweetapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal responsável por iniciar o contexto Spring Boot.
 */
@SpringBootApplication
public class TweetappApplication {

    public static void main(String[] args) {
        SpringApplication.run(TweetappApplication.class, args);
    }
}
