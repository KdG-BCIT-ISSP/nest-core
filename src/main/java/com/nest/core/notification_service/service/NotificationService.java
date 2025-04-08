package com.nest.core.notification_service.service;

import com.nest.core.notification_service.dto.NotificationResponse;
import com.nest.core.notification_service.model.Notification;
import com.nest.core.notification_service.repository.NotificationRepository;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.member_management_service.model.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public SseEmitter subscribe(Long memberId, HttpServletResponse response) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError((e) -> emitters.remove(memberId));

        try {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            // Use findByMemberIdAndReadFalse instead of existsByMemberIdAndReadFalse
            List<Notification> unreadNotifications = notificationRepository.existsByMemberIdAndReadFalse(memberId);
            if (!unreadNotifications.isEmpty()) {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(new NotificationResponse(unreadNotifications.get(0))));
            }
        } catch (IOException e) {
            log.error("Failed to send initial notification for memberId={}", memberId, e);
            emitter.completeWithError(e);
        }

        log.info("SSE subscription established for memberId={}", memberId);
        return emitter;
    }

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeats() {
        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("keepalive"));
            } catch (IOException e) {
                log.warn("Failed to send heartbeat to memberId={}, removing emitter", memberId, e);
                emitter.completeWithError(e);
                emitters.remove(memberId);
            }
        });
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotificationFromAdmin(Long memberId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findAllByMemberId(memberId, pageable);
        return notificationPage.map(NotificationResponse::new);
    }

    @Transactional
    public void createAndSendAnnouncement(String message, boolean isAnnouncement) {
        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            Notification notification = Notification.builder()
                    .member(member)
                    .message(message)
                    .isAnnouncement(isAnnouncement)
                    .createdAt(new Date())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            sendNotification(member.getId(), notification);
        }
    }

    @Transactional
    public void createAndSendNotification(Long memberId, String message) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Notification notification = Notification.builder()
                .member(member)
                .message(message)
                .isAnnouncement(false)
                .createdAt(new Date())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        sendNotification(memberId, notification);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId + " for memberId: " + memberId));
        if (!notification.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Notification does not belong to memberId: " + memberId);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private void sendNotification(Long memberId, Notification notification) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter == null) {
            return;
        }

        try {
            String eventName = notification.isAnnouncement() ? "announcement" : "notification";
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(new NotificationResponse(notification)));
        } catch (IOException e) {
            log.warn("Failed to send notification to memberId={}, removing emitter", memberId, e);
            emitters.remove(memberId);
            emitter.completeWithError(e);
        }
    }
}