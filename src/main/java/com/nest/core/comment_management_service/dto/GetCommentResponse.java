package com.nest.core.comment_management_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.comment_management_service.model.CommentLike;
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
    private JsonNode memberAvatar;
    private String userName;
    private String content;
    private Date createAt;
    private boolean isEdit;
    private Long parentId;
    private Long likesCount;
    private boolean isLiked;

    /**
     * For GetArticleResponse
     *
     * @param comment Comment
     */
    public GetCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.memberAvatar = comment.getMember().getAvatar();
        this.userName = comment.getMember().getUsername();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
        this.isEdit = comment.isEdit();
        this.parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;
        this.likesCount = comment.getLikesCount();
    }

    public GetCommentResponse(Comment comment, Long memberId) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.memberAvatar = comment.getMember().getAvatar();
        this.userName = comment.getMember().getUsername();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
        this.isEdit = comment.isEdit();
        this.parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;
        this.likesCount = comment.getLikesCount();
        this.isLiked = (memberId != null && comment.getCommentLikes().stream().anyMatch(
                commentLike -> commentLike.getMember().getId().equals(memberId)
        ));
    }
}
