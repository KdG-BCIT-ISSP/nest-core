package com.nest.core.post_management_service.dto;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.topic_management_service.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {

    private String title;
    private String content;
    private String type;
    private Long topicId;
    private Set<String> tagNames;

    public Post toEntity(Member member, Topic topic, Set<Tag> tags) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .member(member)
                .topic(topic)
                .type(this.type)
                .tags(tags)
                .build();
    }

}
