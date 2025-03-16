package com.nest.core.topic_management_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.topic_management_service.dto.CreateTopicRequest;
import com.nest.core.topic_management_service.dto.EditTopicRequest;
import com.nest.core.topic_management_service.exceptions.TopicCRUDFailException;
import com.nest.core.topic_management_service.service.TopicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic")
public class TopicApiController {
    private final TopicService topicService;

    @GetMapping("/")
    public ResponseEntity<?> getTopics() {
        try {
            return ResponseEntity.ok(topicService.getAllTopics());
        } catch(Exception e) {
            throw new TopicCRUDFailException("Failed to get topics: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTopic(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody CreateTopicRequest createTopicRequest) {

        if (userDetails instanceof CustomSecurityUserDetails) {
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);
            return ResponseEntity.ok(topicService.createTopic(createTopicRequest, role));

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PutMapping("/update/{topicId}")
    public ResponseEntity<?> updateTopic(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long topicId,
        @RequestBody EditTopicRequest editTopicRequest) {

        if (userDetails instanceof CustomSecurityUserDetails) {
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);
            return ResponseEntity.ok(topicService.updateTopic(topicId, editTopicRequest, role));

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }

    }

    @DeleteMapping("/delete/{topicId}")
    public ResponseEntity<?> deleteTopic(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long topicId) {

        if (userDetails instanceof CustomSecurityUserDetails) {
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);
            topicService.deleteTopic(topicId, role);
            return ResponseEntity.ok("Topic deleted");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }
}
