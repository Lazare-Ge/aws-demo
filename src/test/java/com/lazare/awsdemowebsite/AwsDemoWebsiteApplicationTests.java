package com.lazare.awsdemowebsite;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

//@SpringBootTest
class AwsDemoWebsiteApplicationTests {

    void snsSubscribeExample() {

        SnsClient sns = SnsClient.builder().build();

        String email = "lazare.giorgobiani.lg@gmail.com";

        SubscribeResponse res = sns.subscribe(SubscribeRequest.builder()
                .topicArn("arn:aws:sns:us-east-2:151182332702:lazaregnqUploadsNotificationTopic")
                .protocol("email")
                .endpoint(email)
                .build());

        String subArn = res.subscriptionArn();

        System.out.println("DEBUG!");

    }

    @Test
    void snsUnsubscribeExample(){

        SnsClient sns = SnsClient.builder().build();

        String subArn = "arn:aws:sns:us-east-2:151182332702:lazaregnqUploadsNotificationTopic:5490a0ec-9cdc-4114-a3ec-c7d945cdd67c";

        UnsubscribeResponse res = sns.unsubscribe(UnsubscribeRequest.builder()
                        .subscriptionArn(subArn)
                .build());

        System.out.println("DEBUG!");

    }


    void sqsSendMessageExample(){
        SqsClient sqs = SqsClient.builder().build();

        String body = "Some message!";
        String queueUrl = "https://sqs.us-east-2.amazonaws.com/151182332702/lazaregnqUploadsNotificationQueue";

        SendMessageResponse res = sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build());


        System.out.printf("Message was sent %s\n", res.messageId());

    }
}
