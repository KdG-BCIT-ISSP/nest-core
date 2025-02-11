package com.nest.core.post_management_service.service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.dto.CreateArticleRequest;
import com.nest.core.post_management_service.dto.GetArticleResponse;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.repository.PostRepository;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.tag_management_service.repository.TagRepository;
import com.nest.core.topic_management_service.model.Topic;
import com.nest.core.topic_management_service.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void createArticle(CreateArticleRequest createArticleRequest, Long userId) {

        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        Topic topic = null;
        if (createArticleRequest.getTopicId() != null) {
            topic = topicRepository.findById(createArticleRequest.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
        }

        Set<Tag> tags = createOrFindTags(createArticleRequest.getTagNames());

        Post post = createArticleRequest.toEntity(member, topic, tags);

        postRepository.save(post);
    }

    public List<GetArticleResponse> getArticles() {
        return postRepository.findAll().stream()
                .map(GetArticleResponse::new)
                .collect(Collectors.toList());
    }


    private Set<Tag> createOrFindTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptySet();
        }

        return tagNames.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(null, tagName, new HashSet<>())))
                ).collect(Collectors.toSet());
    }
}
