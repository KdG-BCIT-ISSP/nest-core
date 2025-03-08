package com.nest.core.report_management_service.dto;

import java.sql.Date;

import com.nest.core.report_management_service.model.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCommentReportResponse {
    private Long id;
    private Long memberId;
    private Long commentId;
    private String reason;
    private Date createdAt;

    public GetCommentReportResponse(Report report) {
        this.id = report.getId();
        this.memberId = report.getMember().getId();
        this.commentId = report.getCommentId();
        this.reason = report.getReason();
        this.createdAt = report.getCreatedAt();
    }
}
