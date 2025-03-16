package com.nest.core.topic_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditTopicRequest {
    private String name;
    private String description;
}
