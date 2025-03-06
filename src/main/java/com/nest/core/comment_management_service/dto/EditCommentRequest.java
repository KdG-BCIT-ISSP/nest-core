package com.nest.core.comment_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditCommentRequest {
    private Long id;
    private Long postId;
    private String content;
    private Long memberId;


}
