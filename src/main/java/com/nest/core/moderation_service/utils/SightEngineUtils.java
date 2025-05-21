package com.nest.core.moderation_service.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.Base64;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SightEngineUtils {

    private static List<String> textCategories = Arrays.asList("personal", "link", "spam", "content-trade",
            "money-transaction", "extremism", "violence", "drug", "weapon");
    private static List<String> imageCategories = Arrays.asList("offensive", "nudity", "scam",
            "weapon", "gore", "violence", "recreational_drug");

    public static MultiValueMap<String, String> createTextModerationRequest(String text, String apiUser, String apiSecret) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("mode", "rules");
        requestBody.add("lang", "en,fr,de,nl");
        requestBody.add("categories", "profanity,personal,link,spam,content-trade,money-transaction,extremism,violence,weapon,drug");
        requestBody.add("api_user", apiUser);
        requestBody.add("api_secret", apiSecret);
        return requestBody;
    }

    public static MultiValueMap<String, Object> createImageModerationRequest(String imageBase64, String apiUser, String apiSecret) {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        if (imageBase64.contains(",")) {
            imageBase64 = imageBase64.split(",")[1];
        }
        requestBody.add("media", new ByteArrayResource(Base64.getDecoder().decode(imageBase64.getBytes())) {
            @Override
            public String getFilename() {
                return "image.jpg";
            }
        });
        requestBody.add("models", "nudity-2.1,weapon,recreational_drug,offensive-2.0,scam,text-content,gore-2.0,text,violence");
        requestBody.add("api_user", apiUser);
        requestBody.add("api_secret", apiSecret);
        return requestBody;
    }

    public static String getTextViolationReasons(String jsonString) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);
        Collection<String> reasons = getProfanityReasons(root);

        for(String category : textCategories) {
            Collection<String> categoryReasons = getReasonByCategory(root, category);
            if (!categoryReasons.isEmpty()) {
                reasons.addAll(categoryReasons);
            }
        }
        return String.join(", ", reasons);
    }

    public static String getImageViolationReasons(String jsonString) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonString);
        Collection<String> reasons = new HashSet<>();

        for(String category : imageCategories) {
            String categoryReasons = getImageReasonByCategory(root, category);
            if (!categoryReasons.isEmpty()) {
                reasons.add(categoryReasons);
            }
        }
        return String.join(", ", reasons);
    }

    private static String getImageReasonByCategory(JsonNode root, String category) {
        JsonNode type = root.path(category);
        if (type.has("prob")) {
            double prob = type.path("prob").asDouble();
            if (prob > 0.75) return category;

        } else if (type.has("none")) {
            double prob = type.path("none").asDouble();
            if (prob < 0.75) return category;

        } else if (type.has("classes")) {
            JsonNode classes = type.path("classes");
            for(JsonNode classNode : classes) {
                double prob = classNode.asDouble();
                if (prob > 0.75) return category;
            }
        } else {
            for(JsonNode node : type) {
                double prob = node.asDouble(0);
                if (prob > 0.75) return category;
            }
        }
        return "";
    }

    // Profanity has different JSON structure than the other categories
    private static Collection<String> getProfanityReasons(JsonNode root) {
        Set<String> reasons = new HashSet<>();
        JsonNode matches = root.path("profanity").path("matches");

        if (matches.isArray() && matches.size() > 0) {
            for (JsonNode match : matches) {
                String type = match.path("type").asText();
                String intensity = match.path("intensity").asText();

                if (intensity.equalsIgnoreCase("high") || intensity.equalsIgnoreCase("medium")) {
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
