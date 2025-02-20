package com.nest.core.report_management_service.dto;

import java.sql.Date;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.report_management_service.model.Report;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportPostRequest {

    private String reason;

    public Report toEntity(Member member, Post post) {
        return Report.builder()
                .member(member)
                .reason(this.reason)
                .createdAt(new Date(System.currentTimeMillis()))
                .post(post)
                .build();
    }
}
