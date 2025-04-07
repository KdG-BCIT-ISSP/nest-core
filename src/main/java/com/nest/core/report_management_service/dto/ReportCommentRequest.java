package com.nest.core.report_management_service.dto;

import java.sql.Date;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.report_management_service.model.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportCommentRequest {
    private String reason;

    public Report toEntity(Member member, Comment comment) {
        return Report.builder()
            .member(member)
            .reason(reason)
            .createdAt(new Date(System.currentTimeMillis()))
            .comment(comment)
            .build();
    }
}
