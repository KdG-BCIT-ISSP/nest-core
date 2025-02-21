package com.nest.core.report_management_service.dto;

import java.sql.Date;

import com.nest.core.report_management_service.model.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetArticleReportsResponse {
    private Long id;
    private Long memberId;
    private Long postId;
    private String reason;
    private Date createdAt;

    public GetArticleReportsResponse(Report report) {
        this.id = report.getId();
        this.memberId = report.getMember().getId();
        this.postId = report.getPost().getId();
        this.reason = report.getReason();
        this.createdAt = report.getCreatedAt();
    }
}
