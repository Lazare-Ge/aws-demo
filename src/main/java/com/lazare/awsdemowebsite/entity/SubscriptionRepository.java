package com.lazare.awsdemowebsite.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findFirstByEmail(String email);
    List<SubscriptionEntity> findBySubscriptionArn(String subscriptionArn);
}
