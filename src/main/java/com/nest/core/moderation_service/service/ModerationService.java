package com.nest.core.moderation_service.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nest.core.moderation_service.exception.FailedModerationException;
import com.nest.core.moderation_service.utils.SightEngineUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModerationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${moderation.api-url}")
    private String moderationUrl;
    @Value("${moderation.api-user}")
    private String apiUser;
    @Value("${moderation.api-key}")
    private String apiSecret;

    private static final String textEndpoint = "/text/check.json";
    private static final String imageEndpoint = "/check.json";

    public String checkTextViolation(String text) throws JsonMappingException, JsonProcessingException {
        String url = moderationUrl + textEndpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
            SightEngineUtils.createTextModerationRequest(text, apiUser, apiSecret), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String jsonString = response.getBody();
            log.info(jsonString);
            return SightEngineUtils.getViolationReasons(jsonString);

        } else {
            log.error("Failed to moderate text: " + text);
            throw new FailedModerationException("Moderation service is down");
        }
    }

    public String checkImageViolation(String imageUrl) {
        // NOT IMPLEMENTED YET
        return "NOT IMPLEMENTED YET";
    }
}
