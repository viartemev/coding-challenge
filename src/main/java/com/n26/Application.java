package com.n26;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        System.out.println("Test Hackerrank integration");
        SpringApplication.run(Application.class, args);
    }

}
