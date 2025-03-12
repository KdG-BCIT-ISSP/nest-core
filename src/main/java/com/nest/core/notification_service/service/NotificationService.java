package com.nest.core.notification_service.service;

import com.nest.core.notification_service.model.Notification;
import com.nest.core.notification_service.repository.NotificationRepository;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.member_management_service.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected to notifications"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }

        return emitter;
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

    private void sendNotification(Notification notification) {
        Long memberId = notification.getMember().getId();
        SseEmitter emitter = emitters.get(memberId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification.getMessage()));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }
}
