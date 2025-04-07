package com.nest.core.report_management_service.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.model.MemberRole;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.repository.PostRepository;
import com.nest.core.report_management_service.dto.ReportPostRequest;
import com.nest.core.report_management_service.exception.CommentNotFoundException;
import com.nest.core.report_management_service.exception.PostNotFoundException;
import com.nest.core.report_management_service.exception.ReportDeleteFailException;
import com.nest.core.report_management_service.model.Report;
import com.nest.core.report_management_service.repository.ReportRepository;
import com.nest.core.report_management_service.dto.GetPostReportsResponse;
import com.nest.core.report_management_service.dto.ReportCommentRequest;
import com.nest.core.report_management_service.dto.GetArticleReportsResponse;
import com.nest.core.report_management_service.dto.GetCommentReportResponse;
import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.comment_management_service.repository.CommentRepository;

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
    private final CommentRepository commentRepository;

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
            if (r.getPost() == null) continue; // Skip if not post report

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

    @Transactional
    public void createCommentReport(ReportCommentRequest reportRequest, Long userId, Long commentId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        Comment reportedComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
            
        Report report = reportRequest.toEntity(member, reportedComment);

        // For editing reports if already reported
        Set<Report> memberReports = member.getReports();
        for (Report r : memberReports) {
            if (r.getComment() == null) continue; // Skip if not comment report

            if (r.getComment().getId().equals(commentId)) {
                r.setReason(report.getReason());
                r.setCreatedAt(report.getCreatedAt());
                reportRepository.save(r);
                return;
            }
        }
        // For creating new reports
        member.getReports().add(report);
        member.setReports(member.getReports());
        reportRepository.save(report);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteReport(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ReportDeleteFailException("Report not found"));

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        if (member.getRole() == MemberRole.ADMIN || member.getRole() == MemberRole.MODERATOR || member.getRole() == MemberRole.SUPER_ADMIN) {
            reportRepository.delete(report);
        } else {
            throw new ReportDeleteFailException("Not authorized to delete reports");
        }
    }

    public List<GetPostReportsResponse> getPostReports(Long postId, Long userId) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR && member.getRole() != MemberRole.SUPER_ADMIN) {
            throw new ReportDeleteFailException("Not authorized to view reports");
        }

        return reportRepository.findAllPostReports(postId).stream()
                .map(GetPostReportsResponse::new)
                .collect(Collectors.toList());
    }

    public List<GetPostReportsResponse> getAllPostReports(Long userId) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR && member.getRole() != MemberRole.SUPER_ADMIN) {
            throw new ReportDeleteFailException("Not authorized to view reports");
        }

        return reportRepository.findAllPostReports().stream()
                .map(GetPostReportsResponse::new)
                .collect(Collectors.toList());
    }

    public List<GetArticleReportsResponse> getArticleReports(Long postId, Long userId) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR && member.getRole() != MemberRole.SUPER_ADMIN) {
            throw new ReportDeleteFailException("Not authorized to view reports");
        }

        return reportRepository.findAllArticleReports(postId).stream()
                .map(GetArticleReportsResponse::new)
                .collect(Collectors.toList());
    }

    public List<GetArticleReportsResponse> getAllArticleReports(Long userId) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR && member.getRole() != MemberRole.SUPER_ADMIN) {
            throw new ReportDeleteFailException("Not authorized to view reports");
        }

        return reportRepository.findAllArticleReports().stream()
                .map(GetArticleReportsResponse::new)
                .collect(Collectors.toList());
    }

    public List<GetCommentReportResponse> getAllCommentReports(Long userId) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR && member.getRole() != MemberRole.SUPER_ADMIN) {
            throw new ReportDeleteFailException("Not authorized to view reports");
        }

        return reportRepository.findAllCommentReports().stream()
                .map(GetCommentReportResponse::new)
                .collect(Collectors.toList());
    }
}
