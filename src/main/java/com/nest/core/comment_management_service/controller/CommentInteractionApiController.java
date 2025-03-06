package com.nest.core.comment_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.comment_management_service.service.CommentInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentInteractionApiController {

    private final CommentInteractionService commentInteractionService;

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<Long> getLikes(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentInteractionService.getLikes(commentId));
    }

    @GetMapping("/{commentId}/isLiked")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(commentInteractionService.isLiked(commentId, userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @PostMapping("/{commentId}/toggleLike")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            Long userId = customUser.getUserId();
            return ResponseEntity.ok(commentInteractionService.toggleLike(commentId, userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
}
