package com.nest.core.search_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.nest.core.search_service.exception.SearchFailException;
import com.nest.core.search_service.service.SearchService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchApiController {
    private final SearchService searchService;

    @GetMapping("/post")
    public ResponseEntity<?> searchPost(
        @RequestParam("search_query") String searchQuery,
        @RequestParam("topic") Optional<String> topic,
        @RequestParam("tag") Optional<String> tag,
        @RequestParam("order_by") Optional<String> orderBy,
        @RequestParam("order") Optional<String> order
        ) {
        try {
            return ResponseEntity.ok(searchService.searchPost(searchQuery, topic, tag, orderBy, order));

        } catch(Exception e) {
            throw new SearchFailException("Failed to search: " + e.getMessage());
        }

    }

    @GetMapping("/article")
    public ResponseEntity<?> searchArticle(
        @RequestParam("search_query") String searchQuery,
        @RequestParam("topic") Optional<String> topic,
        @RequestParam("tag") Optional<String> tag,
        @RequestParam("order_by") Optional<String> orderBy,
        @RequestParam("order") Optional<String> order
        ) {
        try {
            return ResponseEntity.ok(searchService.searchArticle(searchQuery, topic, tag, orderBy, order));

        } catch(Exception e) {
            throw new SearchFailException("Failed to search: " + e.getMessage());
        }

    }
}
