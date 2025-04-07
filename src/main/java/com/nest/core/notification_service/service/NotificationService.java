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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId, HttpServletResponse response) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> {
            log.info("Emitter completed for memberId: {}", memberId);
            emitters.remove(memberId);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for memberId: {}", memberId);
            emitters.remove(memberId);
        });

        emitter.onError((Throwable throwable) -> {
            log.error("Error in emitter for memberId: {}", memberId, throwable);
            emitters.remove(memberId);
        });

        try {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("X-Accel-Buffering", "no");

            List<Notification> unreadNotifications = notificationRepository.existsByMemberIdAndReadFalse(memberId);
            if (!unreadNotifications.isEmpty()) {
                emitter.send(SseEmitter.event()
                        .name("new-notification")
                        .data(new NotificationResponse(unreadNotifications.get(0))));
            }

            new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(30000); // Send heartbeat every 30 seconds
                        emitter.send(SseEmitter.event().comment("keepalive"));
                    }
                } catch (IOException e) {
                    log.error("Failed to send heartbeat for memberId: {}", memberId, e);
                    emitter.completeWithError(e);
                } catch (InterruptedException e) {
                    log.info("Heartbeat thread interrupted for memberId: {}", memberId);
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (IOException e) {
            log.error("Failed to send initial notification for memberId: {}", memberId, e);
            emitter.completeWithError(e);
            emitters.remove(memberId);
        }

        return emitter;
    }


    public Page<NotificationResponse> getAllNotificationFromAdmin(Long memberId, Pageable pageable){
        Page<Notification> notificationPage = notificationRepository.findAllByMemberId(memberId, pageable);
        return notificationPage.map(NotificationResponse::new);
    }


    public void createAndSendAnnouncement(String message, boolean isAnnouncement) {
        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            Notification notification = Notification.builder()
                    .member(member)
                    .message(message)
                    .isAnnouncement(isAnnouncement)
                    .createdAt(new Date())
                    .build();
            notificationRepository.save(notification);
            sendNotification(notification);
        }
    }

    public void createAndSendNotification(Long memberId, String message) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Notification notification = Notification.builder()
                .member(member)
                .message(message)
                .build();

        notificationRepository.save(notification);
        sendNotification(notification);
    }

    public void markNotificationAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found : " + notificationId + memberId));

        notification.setRead(true);
        notificationRepository.save(notification);
    }


    private void sendNotification(Notification notification) {
        if (notification.isAnnouncement()) {
            Map<Long, SseEmitter> emittersCopy;
            synchronized (emitters) {
                emittersCopy = new HashMap<>(emitters);
            }

            for (Map.Entry<Long, SseEmitter> entry : emittersCopy.entrySet()) {
                SseEmitter emitter = entry.getValue();
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification.getMessage()));
                } catch (IOException e) {
                    synchronized (emitters) {
                        emitters.remove(entry.getKey());
                    }
                }
            }
        } else {
            Long memberId = notification.getMember().getId();
            SseEmitter emitter;
            synchronized (emitters) {
                emitter = emitters.get(memberId);
            }

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification.getMessage()));
                } catch (IOException e) {
                    synchronized (emitters) {
                        emitters.remove(memberId);
                    }
                }
            }
        }
    }
}
