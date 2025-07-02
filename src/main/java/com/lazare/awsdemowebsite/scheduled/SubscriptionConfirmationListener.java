package com.lazare.awsdemowebsite.scheduled;

import com.lazare.awsdemowebsite.entity.SubscriptionEntity;
import com.lazare.awsdemowebsite.entity.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SubscriptionConfirmationListener {

    private final SnsClient sns;
    private final SubscriptionRepository repo;
    private final String topicArn;

    public SubscriptionConfirmationListener(SnsClient sns,
                                            SubscriptionRepository repo,
                                            @Value("${aws.sns.topicArn}")
                                            String topicArn) {
        this.sns = sns;
        this.repo = repo;
        this.topicArn = topicArn;
    }

    @Scheduled(fixedDelayString = "PT10S")
    public void pollPendingSubscriptions() {

        List<SubscriptionEntity> pending = repo.findBySubscriptionArn("pending confirmation");
        log.info("{} subscriptions are pending for confirmation.", pending.size());
        if (pending.isEmpty()) {
            return;
        }

        ListSubscriptionsByTopicResponse listResp = sns.listSubscriptionsByTopic(
                ListSubscriptionsByTopicRequest.builder()
                        .topicArn(topicArn)
                        .build());

        Map<String, String> arnByEmail = listResp.subscriptions().stream()
                .filter(s -> !s.subscriptionArn().equals("PendingConfirmation"))
                .collect(Collectors.toMap(Subscription::endpoint, Subscription::subscriptionArn));

        for (SubscriptionEntity sub : pending) {
            String realArn = arnByEmail.get(sub.getEmail());
            if (realArn != null) {
                log.info("Subscription for {} is confirmed! (ARN: {})", sub.getEmail(), realArn);
                sub.setSubscriptionArn(realArn);
                repo.save(sub);
            }
        }
    }
}
