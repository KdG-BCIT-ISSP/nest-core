package com.nest.core.moderation_service.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SightEngineUtils {

    private static List<String> categories = Arrays.asList("profanity", "personal", "link", "spam", "content-trade",
            "money-transaction", "extremism", "violence");

    public static MultiValueMap<String, String> createTextModerationRequest(String text, String apiUser, String apiSecret) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("mode", "rules");
        requestBody.add("lang", "en,fr,de,nl");
        requestBody.add("categories", "profanity,personal,link,spam,content-trade,money-transaction,extremism,violence");
        requestBody.add("api_user", apiUser);
        requestBody.add("api_secret", apiSecret);
        return requestBody;
    }

    public static String getFirstViolationReason(String jsonString) throws JsonMappingException, JsonProcessingException {
        String reason = "";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);

        for(String category : categories) {
            reason = getReasonByCategory(root, category);
            if (!reason.isEmpty()) {
                break;
            }
        }
        return reason;
    }

    private static String getReasonByCategory(JsonNode root, String category) {
        String reason = "";
        JsonNode matches = root.path(category).path("matches");

        if (matches.isArray() && matches.size() > 0) {
            JsonNode firstMatch = matches.get(0);
            String intensity = firstMatch.path("intensity").asText();

            if (intensity.equalsIgnoreCase("high")) {
                reason = firstMatch.path("type").asText();
            }
        }
        return reason;
    }
}
