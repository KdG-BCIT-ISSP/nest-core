package com.nest.core.post_management_service.controller;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.post_management_service.dto.CreateArticleRequest;
import com.nest.core.post_management_service.exception.CreateArticleFailException;
import com.nest.core.post_management_service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
