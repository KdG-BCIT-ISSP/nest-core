package com.nest.core.notification_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.notification_service.dto.AnnouncementRequest;
import com.nest.core.notification_service.dto.NotificationRequest;
import com.nest.core.notification_service.dto.NotificationResponse;
import com.nest.core.notification_service.model.Notification;
import com.nest.core.notification_service.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
@Slf4j
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return notificationService.subscribe(userId, response);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to subscribe: " + e.getMessage());
            }
        } else {
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<?> getNotification(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable){
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(notificationService.getAllNotificationFromAdmin(userId, pageable));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.createAndSendNotification(notificationRequest.getMemberId(), notificationRequest.getMessage());
        return "Notification sent!";
    }

    @PostMapping("/announcement/send")
    public String sendNotification(@RequestBody AnnouncementRequest announcementRequest) {
        notificationService.createAndSendAnnouncement(announcementRequest.getMessage(), true);
        return "Announcement sent!";
    }
}
