package com.lazare.awsdemowebsite.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class ClientsConfig {


    @Bean
    AmazonS3 s3Client(){
        return AmazonS3ClientBuilder.defaultClient();
    }

    @Bean
    SqsClient sqsClient(){
        return SqsClient.builder().build();
    }

    @Bean
    SnsClient snsClient(){
        return SnsClient.builder().build();
    }
}
