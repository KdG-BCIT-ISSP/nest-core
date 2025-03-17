package com.nest.core.topic_management_service.dto;

import com.nest.core.topic_management_service.model.Topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetTopicResponse {
    private Long id;
    private String name;
    private String description;

    public GetTopicResponse(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.description = topic.getDescription();
    }
}
