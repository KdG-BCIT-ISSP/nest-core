package com.nest.core.comment_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.comment_management_service.dto.CreateCommentRequest;
import com.nest.core.comment_management_service.dto.EditCommentRequest;
import com.nest.core.comment_management_service.dto.GetCommentResponse;
import com.nest.core.comment_management_service.exception.CreateCommentFailException;
import com.nest.core.comment_management_service.exception.DeleteCommentFailException;
import com.nest.core.comment_management_service.exception.EditCommentFailException;
import com.nest.core.comment_management_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateCommentRequest createCommentRequest) {

        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
                commentService.createComment(userId, createCommentRequest);
                return ResponseEntity.status(HttpStatus.CREATED).body("Comment created");
            } catch (Exception e){
                throw new CreateCommentFailException("Failed to create comment: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PutMapping
    public ResponseEntity<?> editComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditCommentRequest editCommentRequest) {

        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try {
                commentService.editComment(userId, editCommentRequest);
                return ResponseEntity.ok("Comment Edited");
            } catch (Exception e) {
                throw new EditCommentFailException("Failed to edit comment: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long commentId) {
        GetCommentResponse getCommentResponse = commentService.getComment(commentId);
        return ResponseEntity.ok(getCommentResponse);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long commentId) {

        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            String userRole = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            try {
                commentService.deleteComment(userId, commentId, userRole);
                return ResponseEntity.ok("Comment deleted");
            } catch (Exception e) {
                throw new DeleteCommentFailException("Failed to edit comment: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<GetCommentResponse>> getCommentById(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<GetCommentResponse> comments = commentService.getComment(userId, pageable);
        return ResponseEntity.ok(comments);
    }
}
