package com.lazare.awsdemowebsite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class NotificationQueueService {
    private final SqsClient sqs;
    private final String queueUrl;

    public NotificationQueueService(SqsClient sqs, @Value("${aws.sqs.queueUrl}") String queueUrl) {
        this.sqs = sqs; this.queueUrl = queueUrl;
    }

    public void notifyMessage(String message) {

        sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build());
    }
}
