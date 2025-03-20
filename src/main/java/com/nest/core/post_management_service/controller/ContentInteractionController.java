package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.post_management_service.exception.GetArticleFailException;
import com.nest.core.post_management_service.service.ContentInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentInteractionController {
    private final ContentInteractionService interactionService;

    @GetMapping("/{contentId}/likes")
    public ResponseEntity<Long> getLikes(@PathVariable Long contentId) {
        try {
            Long likes = interactionService.getLikes(contentId);
            return ResponseEntity.ok(likes != null ? likes : 0L);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    @GetMapping("/{contentId}/isLiked")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails instanceof CustomSecurityUserDetails customUser) {
                Long userId = customUser.getUserId();
                return ResponseEntity.ok(interactionService.isLiked(contentId, userId));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/{contentId}/toggleLike")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(interactionService.toggleLike(contentId, userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @PostMapping("/{contentId}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long contentId) {
        interactionService.incrementView(contentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contentId}/views")
    public ResponseEntity<Long> getViews(@PathVariable Long contentId) {
        return ResponseEntity.ok(interactionService.getViews(contentId));
    }

    @GetMapping("/article/bookmark")
    public ResponseEntity<?> getBookmarkedArticle(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(interactionService.getAllBookmarkedArticle(userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @GetMapping("/post/bookmark")
    public ResponseEntity<?> getBookmarkedPost(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(interactionService.getAllBookmarkedPost(userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @GetMapping("/id/{contentId}")
    public ResponseEntity<?> getArticle(@PathVariable Long contentId) {
        try {
            return ResponseEntity.ok(interactionService.getContent(contentId));
        } catch (Exception e) {
            throw new GetArticleFailException("Failed to get article: " + e.getMessage());
        }
    }
}
