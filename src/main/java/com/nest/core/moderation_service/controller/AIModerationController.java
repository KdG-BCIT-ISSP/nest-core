package com.nest.core.moderation_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nest.core.moderation_service.service.AIModerationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moderation")
public class AIModerationController {
    private final AIModerationService aiModerationService;

    @PostMapping("/text")
    public ResponseEntity<String> checkText(String text) {
        String checkResults = aiModerationService.checkText(text);
        return ResponseEntity.ok(checkResults);
    }
}
