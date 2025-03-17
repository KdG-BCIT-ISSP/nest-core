package com.nest.core.topic_management_service.dto;

import com.nest.core.topic_management_service.model.Topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTopicRequest {
    private String name;
    private String description;

    public Topic toEntity() {
        return Topic.builder()
                .name(this.name)
                .description(this.description)
                .build();
    }
}
