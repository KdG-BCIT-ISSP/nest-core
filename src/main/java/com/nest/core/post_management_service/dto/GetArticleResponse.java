package com.nest.core.post_management_service.dto;

import com.nest.core.post_management_service.model.Post;
import com.nest.core.tag_management_service.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class GetArticleResponse {
    private Long id;
    private String topicName;
    private String title;
    private String content;
    private String type;
    private Long memberId;
    private String memberUsername;
    private String memberAvatar;
    private Set<String> tagNames;
    private String coverImage;

    public GetArticleResponse(Post post){
        this.id = post.getId();
        this.topicName = post.getTopic().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.type = post.getType();
        this.memberId = post.getMember().getId();
        this.memberUsername = post.getMember().getUsername();
        this.memberAvatar = post.getMember().getAvatar().get("image").asText();
        this.tagNames = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toSet());
        if (post.getExtraData() != null && post.getExtraData().has("imageType") && post.getExtraData().has("imageData")) {
            this.coverImage = post.getExtraData().get("imageType").asText() + "," + post.getExtraData().get("imageData").asText();
        } else {
            this.coverImage = null;
        }
    }
}
