package com.nest.core.search_service.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import com.nest.core.post_management_service.dto.GetArticleResponse;
import com.nest.core.post_management_service.dto.GetPostResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nest.core.post_management_service.model.Post;
import com.nest.core.search_service.repository.SearchRepository;
import com.nest.core.search_service.specification.PostSpecification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final SearchRepository searchRepository;

    public Page<GetPostResponse> searchPost(
            Optional<String> searchQuery,
            Optional<List<String>> topics,
            Optional<List<String>> tags,
            Optional<String> orderBy,
            Optional<String> order,
            Pageable pageable
    ) throws BadRequestException, UnsupportedEncodingException {
        Specification<Post> spec = buildSpecification("post", searchQuery, topics, tags, orderBy, order);

        Page<Post> postPage = searchRepository.findAll(spec, pageable);

        return postPage.map(GetPostResponse::new);
    }

    public Page<GetArticleResponse> searchArticle(
            Optional<String> searchQuery,
            Optional<List<String>> topics,
            Optional<List<String>> tags,
            Optional<String> orderBy,
            Optional<String> order,
            Pageable pageable
    ) throws BadRequestException, UnsupportedEncodingException {
        Specification<Post> spec = buildSpecification("article", searchQuery, topics, tags, orderBy, order);

        Page<Post> articlePage = searchRepository.findAll(spec, pageable);

        return articlePage.map(GetArticleResponse::new);
    }

    private Specification<Post> buildSpecification(
            String type,
            Optional<String> searchQuery,
            Optional<List<String>> topics,
            Optional<List<String>> tags,
            Optional<String> orderBy,
            Optional<String> order
    ) throws BadRequestException, UnsupportedEncodingException {
        String query = searchQuery.orElse("");
        Specification<Post> isType = type.equals("post") ? PostSpecification.isPost() : PostSpecification.isArticle();
        Specification<Post> hasTitle = PostSpecification.hasTitle(URLDecoder.decode(query, "UTF-8"));
        Specification<Post> hasContent = PostSpecification.hasContent(URLDecoder.decode(query, "UTF-8"));

        Specification<Post> mainSpec = Specification.where(isType).and(hasTitle.or(hasContent));
        Specification<Post> topicsSpec = null;
        Specification<Post> tagsSpec = null;

        if (topics.isPresent()) {
            for(String topic : topics.get()) {
                if (topicsSpec == null) topicsSpec = PostSpecification.hasTopic(URLDecoder.decode(topic, "UTF-8"));
                else topicsSpec = topicsSpec.or(PostSpecification.hasTopic(URLDecoder.decode(topic, "UTF-8")));
            }
        }

        if (tags.isPresent()) {
            for(String tag : tags.get()) {
                if (tagsSpec == null) tagsSpec = PostSpecification.hasTag(URLDecoder.decode(tag, "UTF-8"));
                else tagsSpec = tagsSpec.and(PostSpecification.hasTag(URLDecoder.decode(tag, "UTF-8")));
            }
        }
        return mainSpec.and(topicsSpec).and(tagsSpec);
    }
}