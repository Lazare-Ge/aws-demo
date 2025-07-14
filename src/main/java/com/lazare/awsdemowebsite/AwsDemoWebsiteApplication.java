package com.lazare.awsdemowebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwsDemoWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsDemoWebsiteApplication.class, args);
    }
}
