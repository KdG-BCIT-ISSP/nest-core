package com.nest.core.tag_management_service.dto;

import com.nest.core.tag_management_service.model.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetTagResponse {
    private Long id;
    private String name;

    public GetTagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
