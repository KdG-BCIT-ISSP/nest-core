package com.nest.core.comment_management_service.dto;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    private Long postId;
    private String content;
    private Date createAt;

    public Comment toEntity(Post post, Member member) {
        return Comment.builder()
                .post(post)
                .member(member)
                .content(this.content)
                .createAt(this.createAt)
                .build();
    }
}
