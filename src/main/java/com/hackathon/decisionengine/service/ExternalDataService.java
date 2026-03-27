package com.hackathon.decisionengine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class ExternalDataService {

    private final Random random = new Random();

    /**
     * Simulates a call to an external API (e.g., Credit Bureau, Identity Verification).
     * Has a simulated 30% failure rate to test system resilience.
     */
    public void fetchExternalData(String workflowId) {
        log.info("Calling external API for workflow: {}", workflowId);
        
        // Simulate network latency (500ms)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate a 30% chance of the external API crashing or timing out
        if (random.nextInt(10) < 3) {
            log.error("External API connection timed out! Simulating failure...");
            throw new RuntimeException("External API Timeout");
        }
        
        log.info("External API call successful.");
    }
}