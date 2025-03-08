package com.nest.core.report_management_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.report_management_service.dto.ReportCommentRequest;
import com.nest.core.report_management_service.dto.ReportPostRequest;
import com.nest.core.report_management_service.exception.ReportCommentFailException;
import com.nest.core.report_management_service.exception.ReportDeleteFailException;
import com.nest.core.report_management_service.exception.ReportGetFailException;
import com.nest.core.report_management_service.exception.ReportPostFailException;
import com.nest.core.report_management_service.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportApiController {
    private final ReportService reportService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPostReports(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return ResponseEntity.ok(reportService.getPostReports(postId, userId));
            } catch (Exception e) {
                throw new ReportGetFailException("Failed to get post reports: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/post")
    public ResponseEntity<?> getAllPostReports(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return ResponseEntity.ok(reportService.getAllPostReports(userId));
            } catch (Exception e) {
                throw new ReportGetFailException("Failed to get all post reports: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/article/{postId}")
    public ResponseEntity<?> getArticleReports(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return ResponseEntity.ok(reportService.getArticleReports(postId, userId));
            } catch (Exception e) {
                throw new ReportGetFailException("Failed to get article reports: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/article")
    public ResponseEntity<?> getAllArticleReports(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return ResponseEntity.ok(reportService.getAllArticleReports(userId));
            } catch (Exception e) {
                throw new ReportGetFailException("Failed to get all article reports: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long reportId) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                reportService.deleteReport(reportId, userId);
                return ResponseEntity.ok("Report deleted");
            } catch (Exception e) {
                throw new ReportDeleteFailException("Failed to delete report: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> reportPost(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ReportPostRequest report,
        @PathVariable Long postId) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                reportService.createPostReport(report, userId, postId);
                return ResponseEntity.ok("Post reported");
            } catch (Exception e) {
                throw new ReportPostFailException("Failed to report post: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PostMapping("/article/{postId}")
    public ResponseEntity<?> reportArticle(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ReportPostRequest report,
        @PathVariable Long postId) {
        return reportPost(userDetails, report, postId);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> reportComment(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ReportCommentRequest report,
        @PathVariable Long commentId) {

            if (userDetails instanceof CustomSecurityUserDetails customUser) {
                Long userId = customUser.getUserId();
                try {
                    reportService.createCommentReport(report, userId, commentId);
                    return ResponseEntity.ok("Comment reported");

                } catch (Exception e) {
                    throw new ReportCommentFailException("Failed to report comment: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
            }
    }

    @GetMapping("/comment")
    public ResponseEntity<?> getAllCommentReports(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            try {
                return ResponseEntity.ok(reportService.getAllCommentReports(userId));
            } catch (Exception e) {
                throw new ReportGetFailException("Failed to get all comment reports: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }
}
