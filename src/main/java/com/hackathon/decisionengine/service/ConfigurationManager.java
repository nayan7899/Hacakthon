package com.hackathon.decisionengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.decisionengine.domain.rules.Rule;
import com.hackathon.decisionengine.domain.rules.WorkflowConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationManager {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResourceLoader resourceLoader;
    
    // In-memory cache so we don't read the file on every single API request
    private final Map<String, WorkflowConfig> configCache = new ConcurrentHashMap<>();

    public List<Rule> getRulesForWorkflow(String workflowId) {
        if (!configCache.containsKey(workflowId)) {
            loadConfigIntoCache(workflowId);
        }
        return configCache.get(workflowId).getRules();
    }

    private void loadConfigIntoCache(String workflowId) {
        try {
            Resource resource = resourceLoader.getResource("classpath:workflows/" + workflowId + ".json");
            WorkflowConfig config = objectMapper.readValue(resource.getInputStream(), WorkflowConfig.class);
            configCache.put(workflowId, config);
            log.info("Successfully loaded configuration for workflow: {}", workflowId);
        } catch (IOException e) {
            log.error("Failed to load configuration for workflow: {}", workflowId, e);
            throw new IllegalArgumentException("Workflow configuration not found for: " + workflowId);
        }
    }
}