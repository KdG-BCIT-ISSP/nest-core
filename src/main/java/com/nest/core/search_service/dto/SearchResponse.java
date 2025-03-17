package com.nest.core.search_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.nest.core.post_management_service.model.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResponse {
    private Long id;
    private String title;
    private String creatorName;
    private String content;
    private String topic;
    private LocalDateTime createdAt;
    private List<String> tags;
    private int shareCount;
    private Long likesCount;
    private Long viewCount;
    private byte[] postImage;


    public SearchResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.creatorName = post.getMember().getUsername();
        this.content = post.getContent();
        this.likesCount = post.getLikesCount();
        this.shareCount = post.getShareCount();
        this.viewCount = post.getViewCount();
        this.tags = post.getPostTags().stream().map(postTag -> postTag.getTag().getName()).toList();
        this.topic = post.getTopic().getName();
        this.createdAt = post.getCreatedAt();
        this.postImage = post.getPostImages().stream().findFirst().map(postImage -> postImage.getImageData()).orElse(null);
    }
}
