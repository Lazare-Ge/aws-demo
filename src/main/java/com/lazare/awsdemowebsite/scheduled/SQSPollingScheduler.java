package com.lazare.awsdemowebsite.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SQSPollingScheduler {


    private final SqsClient sqs;
    private final SnsClient sns;

    private final String queueUrl;
    private final String topicArn;

    public SQSPollingScheduler(SqsClient sqs,
                               SnsClient sns,
                               @Value("${aws.sqs.queueUrl}") String queueUrl,
                               @Value("${aws.sns.topicArn}") String topicArn) {
        this.sqs = sqs;
        this.sns = sns;
        this.queueUrl = queueUrl;
        this.topicArn = topicArn;
    }

    @Scheduled(fixedDelayString = "PT10S")
    public void pollAndPublish() {
        log.info("Initiating message polling!");
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .build();

        ReceiveMessageResponse receiveResponse = sqs.receiveMessage(receiveRequest);
        List<Message> messages = receiveResponse.messages();
        log.info("Polling {} messages!", messages.size());
        if (messages.isEmpty()) {
            return;
        }



        for (Message msg : messages) {
            PublishRequest pubReq = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(msg.body())
                    .build();
            sns.publish(pubReq);
        }

        List<DeleteMessageBatchRequestEntry> deleteEntries = messages.stream()
                .map(msg -> DeleteMessageBatchRequestEntry.builder()
                        .id(msg.messageId())
                        .receiptHandle(msg.receiptHandle())
                        .build())
                .collect(Collectors.toList());

        DeleteMessageBatchRequest deleteRequest = DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(deleteEntries)
                .build();

        sqs.deleteMessageBatch(deleteRequest);
    }
}
