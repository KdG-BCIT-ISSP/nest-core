package com.nest.core.post_management_service.dto;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.topic_management_service.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    private String title;
    private String content;
    private String type;
    private Long topicId;
    private Set<String> tagNames;
    private List<String> imageBase64;

    public Post toEntity(Member member, Topic topic){
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .type(this.type)
                .topic(topic)
                .member(member)
                .postTags(new HashSet<>())
                .build();
    }
}
