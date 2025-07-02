package com.lazare.awsdemowebsite.service;

import com.lazare.awsdemowebsite.entity.SubscriptionEntity;
import com.lazare.awsdemowebsite.entity.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.time.Instant;

@Service
public class NotificationSubscriptionService {

    private final SnsClient sns;
    private final String topicArn;
    private final SubscriptionRepository subscriptionRepository;

    public NotificationSubscriptionService(SnsClient sns, @Value("${aws.sns.topicArn}") String topicArn, SubscriptionRepository subscriptionRepository){
        this.sns = sns;
        this.topicArn = topicArn;
        this.subscriptionRepository = subscriptionRepository;
    }

    public String subscribe(String email){

        SubscribeResponse res = sns.subscribe(SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build());

        SubscriptionEntity saved = subscriptionRepository.save(new SubscriptionEntity(null, email, res.subscriptionArn(), Instant.now()));

        return res.subscriptionArn();

    }

    public String unsubscribe(String email) {
        SubscriptionEntity sub = subscriptionRepository.findFirstByEmail(email).orElseThrow(() -> new RuntimeException("Sub was not found for mail!"));

        if(sub.getSubscriptionArn().startsWith("arn")) {
            sns.unsubscribe(UnsubscribeRequest.builder()
                .subscriptionArn(sub.getSubscriptionArn())
                .build());
        }

        subscriptionRepository.delete(sub);

        return email;

    }


}
