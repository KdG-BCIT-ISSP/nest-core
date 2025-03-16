package com.nest.core.tag_management_service.dto;

import com.nest.core.tag_management_service.model.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTagRequest {
    private String name;

    public Tag toEntity() {
        return Tag.builder()
                .name(this.name)
                .build();
    }
}
