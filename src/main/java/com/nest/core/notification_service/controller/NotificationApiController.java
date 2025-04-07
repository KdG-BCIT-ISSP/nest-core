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
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                SseEmitter emitter = notificationService.subscribe(userId, response);
                return ResponseEntity.ok().header("Content-Type", "text/event-stream").header("Cache-Control", "no-cache").header("Connection", "keep-alive").body(emitter);
            } catch (Exception e) {
                log.error("Failed to subscribe user {}: {}", userId, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            notificationService.markNotificationAsRead(notificationId, userId);
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }
}
