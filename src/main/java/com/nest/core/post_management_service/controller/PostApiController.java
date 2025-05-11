package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.moderation_service.exception.ContentViolationException;
import com.nest.core.moderation_service.service.ModerationService;
import com.nest.core.post_management_service.dto.CreatePostRequest;
import com.nest.core.post_management_service.dto.EditPostRequest;
import com.nest.core.post_management_service.exception.*;
import com.nest.core.post_management_service.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {
    private final PostService postService;
    private final ModerationService moderationService;

    @PostMapping
    public ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreatePostRequest
            createPostRequest) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
                String titleReasons = moderationService.checkTextViolation(createPostRequest.getTitle());
                if (!titleReasons.isEmpty()) {
                    throw new ContentViolationException("Your post title was flagged by our moderation service for the following violations: " + titleReasons);
                }
                String contentReasons = moderationService.checkTextViolation(createPostRequest.getContent());
                if (!contentReasons.isEmpty()) {
                    throw new ContentViolationException("Your post content was flagged by our moderation service for the following violations: " + contentReasons);
                }
                for(String image : createPostRequest.getImageBase64()) {
                    String imageReasons = moderationService.checkImageViolation(image);
                    if (!imageReasons.isEmpty()) {
                        throw new ContentViolationException("Your post image was flagged by our moderation service for the following violations: " + imageReasons);
                    }
                }
                postService.createPost(createPostRequest, userId);
                return ResponseEntity.ok("Post created");
            } catch (Exception e){
                throw new CreatePostFailException("Failed to create post: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping
    public ResponseEntity<?> getPosts(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        Long userId = null;

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            userId = customUser.getUserId();
        }
        try {
            return ResponseEntity.ok(postService.getPosts(userId, pageable));
        } catch (Exception e) {
            throw new GetPostFailException("Failed to get articles: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getPostStats(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails) {
            String userRole = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            try {
                return ResponseEntity.ok(postService.getPostStats(userRole));
            } catch (Exception e) {
                throw new GetPostFailException("Failed to get post stats: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PutMapping
    public ResponseEntity<?> editPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditPostRequest editPostRequest) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
                String titleReasons = moderationService.checkTextViolation(editPostRequest.getTitle());
                if (!titleReasons.isEmpty()) {
                    throw new ContentViolationException("Your post title was flagged by our moderation service for the following violations: " + titleReasons);
                }
                String contentReasons = moderationService.checkTextViolation(editPostRequest.getContent());
                if (!contentReasons.isEmpty()) {
                    throw new ContentViolationException("Your post content was flagged by our moderation service for the following violations: " + contentReasons);
                }
                for(String image : editPostRequest.getImageBase64()) {
                    String imageReasons = moderationService.checkImageViolation(image);
                    if (!imageReasons.isEmpty()) {
                        throw new ContentViolationException("Your post image was flagged by our moderation service for the following violations: " + imageReasons);
                    }
                }
                return ResponseEntity.ok(postService.editPost(editPostRequest, userId));
            } catch (Exception e){
                throw new EditArticleFailException("Failed to edit post: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            String userRole = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            try{
                postService.deletePost(postId, userId, userRole);
                return ResponseEntity.ok("Post deleted");
            } catch (Exception e){
                throw new EditArticleFailException("Failed to delete post: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPostByUserId(@PathVariable Long userId){

        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }
}
