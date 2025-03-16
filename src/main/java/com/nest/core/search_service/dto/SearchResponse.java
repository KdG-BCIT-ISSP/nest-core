package com.nest.core.search_service.dto;

import com.nest.core.post_management_service.model.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResponse {
    private Long id;

    public SearchResponse(Post post) {
        this.id = post.getId();
    }
}
