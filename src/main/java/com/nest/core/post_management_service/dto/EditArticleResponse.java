package com.nest.core.post_management_service.dto;

import com.nest.core.post_management_service.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditArticleResponse {
    private Long id;
    private String topicName;
    private String title;
    private String content;
    private String type;
    private Long memberId;
    private String memberUsername;
    private String memberAvatar;
    private Set<String> tagNames;

    public EditArticleResponse(Post post){
        this.id = post.getId();
        this.topicName = post.getTopic().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.type = post.getType();
        this.memberId = post.getMember().getId();
        this.memberUsername = post.getMember().getUsername();
        this.memberAvatar = post.getMember().getAvatar();
        this.tagNames = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toSet());
    }
}

