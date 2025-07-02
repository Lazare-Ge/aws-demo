package com.lazare.awsdemowebsite;

import com.lazare.awsdemowebsite.service.NotificationSubscriptionService;
import com.lazare.awsdemowebsite.scheduled.SubscriptionConfirmationListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwsDemoWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsDemoWebsiteApplication.class, args);
    }

    @Bean
    CommandLineRunner cmdRunner(NotificationSubscriptionService service, SubscriptionConfirmationListener listener){
        return args -> {
//            String mail = "lazare.giorgobiani.lg@gmail.com";
//            service.subscribe("lazare.giorgobiani.lg@gmail.com");
            listener.pollPendingSubscriptions();

            System.out.println("DEBUG!");
        };

    }
}
