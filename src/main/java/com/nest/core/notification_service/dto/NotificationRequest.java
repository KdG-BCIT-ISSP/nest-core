package com.nest.core.notification_service.dto;

import lombok.Getter;

@Getter
public class NotificationRequest {
    private Long memberId;
    private String message;
}
