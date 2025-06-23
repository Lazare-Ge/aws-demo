package com.lazare.awsdemowebsite;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AwsDemoWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsDemoWebsiteApplication.class, args);
    }

    @Bean
    AmazonS3 s3Client(){
        return AmazonS3ClientBuilder.defaultClient();
    }

}
