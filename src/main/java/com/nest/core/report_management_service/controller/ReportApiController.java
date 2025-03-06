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
import com.nest.core.report_management_service.dto.ReportPostRequest;
import com.nest.core.report_management_service.exception.ReportPostFailException;
import com.nest.core.report_management_service.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportApiController {
    private final ReportService reportService;

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
}
