package com.nest.core.comment_management_service.dto;

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
}
