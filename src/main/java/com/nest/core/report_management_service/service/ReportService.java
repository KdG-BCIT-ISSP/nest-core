package com.nest.core.report_management_service.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.repository.PostRepository;
import com.nest.core.report_management_service.dto.ReportPostRequest;
import com.nest.core.report_management_service.exception.PostNotFoundException;
import com.nest.core.report_management_service.model.Report;
import com.nest.core.report_management_service.repository.ReportRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createPostReport(ReportPostRequest reportRequest, Long userId, Long postId) {

        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        Report report = reportRequest.toEntity(member, post);

        Set<Report> memberReports = member.getReports();
        for (Report r : memberReports) {
            if (r.getPost().getId().equals(postId)) {
                r.setReason(report.getReason());
                r.setCreatedAt(report.getCreatedAt());
                reportRepository.save(r);
                return;
            }
        }
        member.getReports().add(report);
        member.setReports(member.getReports());
        reportRepository.save(report);
        memberRepository.save(member);
    }
}
