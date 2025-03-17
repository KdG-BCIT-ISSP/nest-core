package com.nest.core.search_service.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import com.nest.core.post_management_service.model.Post;
import com.nest.core.search_service.dto.SearchResponse;
import com.nest.core.search_service.repository.SearchRepository;
import com.nest.core.search_service.specification.PostSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final SearchRepository searchRepository;

    public List<SearchResponse> searchPost(
        Optional<String> searchQuery,
        Optional<List<String>> topics,
        Optional<List<String>> tags,
        Optional<String> orderBy,
        Optional<String> order
        ) throws BadRequestException, UnsupportedEncodingException {

        Specification<Post> spec = buildSpecification("post", searchQuery, topics, tags, orderBy, order);
        Sort sort = PostSpecification.sortBy(orderBy.orElse("id"), order.orElse("ASC"));

        return searchRepository.findAll(spec, sort).stream()
                .map(SearchResponse::new)
                .collect(Collectors.toList());
    }

    public List<SearchResponse> searchArticle(
        Optional<String> searchQuery,
        Optional<List<String>> topics,
        Optional<List<String>> tags,
        Optional<String> orderBy,
        Optional<String> order
        ) throws BadRequestException, UnsupportedEncodingException {

        Specification<Post> spec = buildSpecification("article", searchQuery, topics, tags, orderBy, order);
        Sort sort = PostSpecification.sortBy(orderBy.orElse("id"), order.orElse("ASC"));

        return searchRepository.findAll(spec, sort).stream()
                .map(SearchResponse::new)
                .collect(Collectors.toList());
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