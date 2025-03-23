package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.member_management_service.model.MemberRole;
import com.nest.core.post_management_service.dto.CreateArticleRequest;
import com.nest.core.post_management_service.dto.EditArticleRequest;
import com.nest.core.post_management_service.dto.EditArticleResponse;
import com.nest.core.post_management_service.exception.AddBookmarkFailException;
import com.nest.core.post_management_service.exception.CreateArticleFailException;
import com.nest.core.post_management_service.exception.DeleteArticleFailException;
import com.nest.core.post_management_service.exception.EditArticleFailException;
import com.nest.core.post_management_service.exception.GetArticleFailException;
import com.nest.core.post_management_service.exception.RemoveBookmarkFailException;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleApiController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<?> createArticle(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateArticleRequest createArticleRequest) {

        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try{
                articleService.createArticle(createArticleRequest, userId);
                return ResponseEntity.ok("Article created");
            } catch (Exception e){
                throw new CreateArticleFailException("Failed to create article: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @GetMapping
    public ResponseEntity<?> getArticles(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = null;

        if (userDetails instanceof CustomSecurityUserDetails customUser) {
            userId = customUser.getUserId();
        }

        try {
            return ResponseEntity.ok(articleService.getArticles(userId));
        } catch (Exception e) {
            throw new GetArticleFailException("Failed to get articles: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editArticle(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditArticleRequest editArticleRequest) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            try {
                EditArticleResponse editedPost = articleService.editArticle(editArticleRequest, userId);
                return ResponseEntity.ok(editedPost);
            } catch (Exception e) {
                throw new EditArticleFailException("Failed to edit article: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteArticle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        if (userDetails instanceof CustomSecurityUserDetails customUser){
            Long userId = customUser.getUserId();
            String userRole = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            try {
                articleService.deleteArticle(postId, userId, userRole);
                return ResponseEntity.ok("Article deleted");
            } catch (Exception e) {
                throw new DeleteArticleFailException("Failed to delete article: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

}
