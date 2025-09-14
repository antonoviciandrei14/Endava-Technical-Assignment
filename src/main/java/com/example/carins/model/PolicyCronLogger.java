package com.example.carins.model;

import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PolicyCronLogger {

    private static final Logger logger = LoggerFactory.getLogger(PolicyCronLogger.class);

    private final InsurancePolicyRepository policyRepository;

    private final Set<Long> loggedPolicies = ConcurrentHashMap.newKeySet();

    public PolicyCronLogger(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }


    @Scheduled(fixedRate =  5 * 1000) //la 5 secunde
    public void logExpiredPolicies() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<InsurancePolicy> expired = policyRepository.findByEndDate(yesterday);

        for (InsurancePolicy policy : expired) {
            if (loggedPolicies.add(policy.getId())) {
                logger.warn("Policy {} for car {} expired on {}",
                        policy.getId(),
                        policy.getCar().getId(),
                        policy.getEndDate());
            }
        }
    }
}
