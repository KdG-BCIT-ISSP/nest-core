package com.nest.core.post_management_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.topic_management_service.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.HashSet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {

    private String title;
    private String content;
    private String type;
    private Long topicId;
    private Set<String> tagNames;
    private String coverImage;

    public Post toEntity(Member member, Topic topic) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .member(member)
                .topic(topic)
                .type(this.type)
                .postTags(new HashSet<>())
                .extraData(parseCoverImage())
                .build();
    }

    private JsonNode parseCoverImage() {
        if (coverImage == null || !coverImage.contains(",")) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode imageNode = objectMapper.createObjectNode();

        String[] parts = coverImage.split(",", 2);
        imageNode.put("imageType", parts[0]);
        imageNode.put("imageData", parts[1]);

        return imageNode;
    }
}

