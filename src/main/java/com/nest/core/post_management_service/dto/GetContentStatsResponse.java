package com.nest.core.post_management_service.dto;

import java.time.LocalDateTime;

import com.nest.core.post_management_service.model.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetContentStatsResponse {
    private String title;
    private String topicName;
    private Long likesCount;
    private Long viewCount;
    private int bookmarkCount;
    private int shareCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public GetContentStatsResponse(Post post) {
        this.title = post.getTitle();
        this.topicName = post.getTopic().getName();
        this.likesCount = post.getLikesCount();
        this.viewCount = post.getViewCount();
        this.bookmarkCount = post.getBookmarkedMembers().size();
        this.shareCount = post.getShareCount();
        this.commentCount = post.getComments().size();
        this.createdAt = post.getCreatedAt();
    }
}
