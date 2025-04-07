package com.nest.core.notification_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementRequest {
    private String message;
    private boolean isAnnouncement;

}
