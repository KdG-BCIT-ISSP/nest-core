package com.nest.core.moderation_service.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AIModerationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${moderation.api-url}")
    private String moderationUrl;
    @Value("${moderation.api-user}")
    private String apiUser;
    @Value("${moderation.api-key}")
    private String apiSecret;

    private static final String textEndpoint = "/text/check.json";
    private static final String imageEndpoint = "/check.json";

    public String checkText(String text) {
        String url = moderationUrl + textEndpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(createTextModerationRequest(text), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    // Need to discuss with team if this one is necessary
    public String checkImage(String imageUrl) {
        // NOT IMPLEMENTED YET
        return "NOT IMPLEMENTED YET";
    }

    private MultiValueMap<String, String> createTextModerationRequest(String text) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("mode", "rules");
        requestBody.add("lang", "en,fr,de,nl");
        requestBody.add("categories", "profanity,personal,link,spam,content-trade,money-transaction,extremism,violence");
        requestBody.add("api_user", apiUser);
        requestBody.add("api_secret", apiSecret);
        return requestBody;
    }
}
