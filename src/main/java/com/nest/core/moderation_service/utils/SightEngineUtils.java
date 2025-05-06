package com.nest.core.moderation_service.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SightEngineUtils {

    private static List<String> categories = Arrays.asList("personal", "link", "spam", "content-trade",
            "money-transaction", "extremism", "violence", "drug", "self-harm", "weapon");

    public static MultiValueMap<String, String> createTextModerationRequest(String text, String apiUser, String apiSecret) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("mode", "rules");
        requestBody.add("lang", "en,fr,de,nl");
        requestBody.add("categories", "profanity,personal,link,spam,content-trade,money-transaction,extremism,violence,weapon,drug,self-harm");
        requestBody.add("api_user", apiUser);
        requestBody.add("api_secret", apiSecret);
        return requestBody;
    }

    public static String getViolationReasons(String jsonString) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);
        Collection<String> reasons = getProfanityReasons(root);

        for(String category : categories) {
            Collection<String> categoryReasons = getReasonByCategory(root, category);
            if (!categoryReasons.isEmpty()) {
                reasons.addAll(categoryReasons);
            }
        }
        return String.join(", ", reasons);
    }

    // Profanity has different JSON structure than the other categories
    private static Collection<String> getProfanityReasons(JsonNode root) {
        Set<String> reasons = new HashSet<>();
        JsonNode matches = root.path("profanity").path("matches");

        if (matches.isArray() && matches.size() > 0) {
            for (JsonNode match : matches) {
                String type = match.path("type").asText();
                String intensity = match.path("intensity").asText();

                if (intensity.equalsIgnoreCase("high")) {
                    reasons.add(type);
                }
            }
        }
        return reasons;
    }

    private static Set<String> getReasonByCategory(JsonNode root, String category) {
        Set<String> reasons = new HashSet<>();
        JsonNode matches = root.path(category).path("matches");

        if (matches.isArray() && matches.size() > 0) {
            for (JsonNode match : matches) {
                String type = match.path("type").asText();
                reasons.add(type);
            }
        }
        return reasons;
    }
}
