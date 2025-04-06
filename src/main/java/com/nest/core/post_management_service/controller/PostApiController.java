package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.post_management_service.dto.CreatePostRequest;
import com.nest.core.post_management_service.dto.EditPostRequest;
import com.nest.core.post_management_service.exception.*;
import com.nest.core.post_management_service.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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

    @PostMapping
    public ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreatePostRequest
            createPostRequest) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
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
    public ResponseEntity<?> getPosts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = null;

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            userId = customUser.getUserId();
        }
        try {
            return ResponseEntity.ok(postService.getPosts(userId));
        } catch (Exception e) {
            throw new GetPostFailException("Failed to get articles: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditPostRequest editPostRequest) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
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
