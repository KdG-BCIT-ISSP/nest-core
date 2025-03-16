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
        String searchQuery,
        Optional<String> topic,
        Optional<String> tag,
        Optional<String> orderBy,
        Optional<String> order
        ) throws BadRequestException, UnsupportedEncodingException {

        if (!isValidRequest(orderBy, order)) {
            throw new BadRequestException("Order by and order must be provided together or not at all");
        }

        Specification<Post> isPost = PostSpecification.isPost();
        Specification<Post> hasTitle = PostSpecification.hasTitle(URLDecoder.decode(searchQuery, "UTF-8"));
        Specification<Post> hasContent = PostSpecification.hasContent(URLDecoder.decode(searchQuery, "UTF-8"));

        Specification<Post> spec = Specification.where(isPost).and(hasTitle.or(hasContent));

        if (topic.isPresent()) spec = spec.and(PostSpecification.hasTopic(URLDecoder.decode(topic.get(), "UTF-8")));
        if (tag.isPresent()) spec = spec.and(PostSpecification.hasTag(URLDecoder.decode(tag.get(), "UTF-8")));

        Sort sort = PostSpecification.sortBy(orderBy.orElse("id"), order.orElse("ASC"));

        return searchRepository.findAll(spec, sort).stream()
                .map(SearchResponse::new)
                .collect(Collectors.toList());
    }

    public List<SearchResponse> searchArticle(
        String searchQuery,
        Optional<String> topic,
        Optional<String> tag,
        Optional<String> orderBy,
        Optional<String> order
        ) throws BadRequestException, UnsupportedEncodingException {

        if (!isValidRequest(orderBy, order)) {
            throw new BadRequestException("Order by and order must be provided together or not at all");
        }

        Specification<Post> isArticle = PostSpecification.isArticle();
        Specification<Post> hasTitle = PostSpecification.hasTitle(URLDecoder.decode(searchQuery, "UTF-8"));
        Specification<Post> hasContent = PostSpecification.hasContent(URLDecoder.decode(searchQuery, "UTF-8"));

        Specification<Post> spec = Specification.where(isArticle).and(hasTitle.or(hasContent));

        if (topic.isPresent()) spec = spec.and(PostSpecification.hasTopic(URLDecoder.decode(topic.get(), "UTF-8")));
        if (tag.isPresent()) spec = spec.and(PostSpecification.hasTag(URLDecoder.decode(tag.get(), "UTF-8")));

        Sort sort = PostSpecification.sortBy(orderBy.orElse("id"), order.orElse("ASC"));

        return searchRepository.findAll(spec, sort).stream()
                .map(SearchResponse::new)
                .collect(Collectors.toList());
    }

    private boolean isValidRequest(Optional<String> orderBy, Optional<String> order) {
        if (orderBy.isPresent() && order.isPresent() || !orderBy.isPresent() && !order.isPresent()) {
            return true;
        }
        return false;
    }
}
