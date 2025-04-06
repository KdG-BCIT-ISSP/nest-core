package com.nest.core.notification_service.dto;

import com.nest.core.notification_service.model.Notification;
import lombok.Getter;

import java.util.Date;

@Getter
public class NotificationResponse {
    private final String userName;
    private final String message;
    private final Date timestamp;
    private final boolean isAnnouncement;
    private final boolean isRead;

    public NotificationResponse(Notification notification){
        this.userName = notification.getMember().getUsername();
        this.message = notification.getMessage();
        this.timestamp = notification.getCreatedAt();
        this.isAnnouncement = notification.isAnnouncement();
        this.isRead = notification.isRead();
    }
}
