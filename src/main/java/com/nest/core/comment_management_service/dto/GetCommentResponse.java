package com.nest.core.comment_management_service.dto;

import com.nest.core.comment_management_service.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCommentResponse {
    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private Date createAt;
    private boolean isEdit;
    private Long parentId;

    /**
     * For GetArticleResponse
     *
     * @param comment Comment
     */
    public GetCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
        this.isEdit = comment.isEdit();
        this.parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;
    }
}
