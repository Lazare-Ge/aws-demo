package com.lazare.awsdemowebsite.controller;

import com.lazare.awsdemowebsite.service.NotificationSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationSubscriptionController {

    private final NotificationSubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam("email") String email) {
        String subscriptionArn = subscriptionService.subscribe(email);
        return ResponseEntity.accepted()
                .body("Subscription requested for '" + email + "'. Please confirm via the email sent. ARN: " + subscriptionArn);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam("email") String email) {
        subscriptionService.unsubscribe(email);
        return ResponseEntity.ok("Unsubscribed email: '" + email + "'");
    }
}
