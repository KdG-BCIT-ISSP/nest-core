package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
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
        return ResponseEntity.ok(interactionService.getLikes(contentId));
    }

    @GetMapping("/{contentId}/isLiked")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(interactionService.isLiked(contentId, userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
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
}
