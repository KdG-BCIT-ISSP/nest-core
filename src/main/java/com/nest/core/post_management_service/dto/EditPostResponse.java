package com.nest.core.post_management_service.dto;

import com.nest.core.post_management_service.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditPostResponse {
    private Long id;
    private Long memberId;
    private String title;
    private String content;
    private String type;
    private Long topicId;
    private Set<String> tagNames;
    private List<String> imageBase64;

    public EditPostResponse(Post post){
        this.id = post.getId();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.type = post.getType();
        this.topicId = post.getTopic().getId();
        this.tagNames = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toSet());
        this.imageBase64 = post.getPostImages().stream()
                .map(image -> image.getImageType() + "," + Base64.getEncoder().encodeToString(image.getImageData()))
                .collect(Collectors.toList());
    }
}
