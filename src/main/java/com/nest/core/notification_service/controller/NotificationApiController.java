package com.nest.core.notification_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.notification_service.dto.NotificationRequest;
import com.nest.core.notification_service.model.Notification;
import com.nest.core.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return notificationService.subscribe(userId);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to subscribe: " + e.getMessage());
            }
        } else {
            return null;
        }
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.createAndSendNotification(notificationRequest.getMemberId(), notificationRequest.getMessage());
        return "Notification sent!";
    }
}
