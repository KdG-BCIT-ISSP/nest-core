package com.nest.core.post_management_service.service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.dto.CreatePostRequest;
import com.nest.core.post_management_service.exception.CreatePostFailException;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostImage;
import com.nest.core.post_management_service.model.PostTag;
import com.nest.core.post_management_service.repository.PostImageRepository;
import com.nest.core.post_management_service.repository.PostRepository;
import com.nest.core.post_management_service.repository.PostTagRepository;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.tag_management_service.repository.TagRepository;
import com.nest.core.topic_management_service.model.Topic;
import com.nest.core.topic_management_service.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;
    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public void createPost(CreatePostRequest createPostRequest, Long userId) {
        Member member = findMemberById(userId);
        Topic topic = findTopicById(createPostRequest.getTopicId());
        Post createdPost = createAndSavePost(createPostRequest, member, topic);
        Set<Tag> tags = createOrFindTags(createPostRequest.getTagNames());
        savePostTags(createdPost, tags);
        saveImages(createPostRequest, createdPost);
    }

    private Member findMemberById(Long userId) {
        return memberRepository
                .findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    private Topic findTopicById(Long topicId) {
        if (topicId == null) {
            return null;
        }
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    private void savePostTags(Post createdPost, Set<Tag> tags) {
        Set<PostTag> postTags = tags.stream()
                .map(tag -> new PostTag(createdPost, tag))
                .collect(Collectors.toSet());
        postTagRepository.saveAll(postTags);
    }

    private Post createAndSavePost(CreatePostRequest createPostRequest, Member member, Topic topic) {
        return postRepository.save(createPostRequest.toEntity(member, topic));
    }

    private void saveImages(CreatePostRequest createPostRequest, Post createdPost) {
        if (createPostRequest.getImageBase64() == null || createPostRequest.getImageBase64().isEmpty()) {
            return;
        }

        int imageCount = Math.min(createPostRequest.getImageBase64().size(), 3);
        for (int i = 0; i < imageCount; i++) {
            try {
                byte[] imageBytes = decodeBase64Image(createPostRequest.getImageBase64().get(i));
                PostImage postImage = PostImage.builder()
                        .post(createdPost)
                        .imageData(imageBytes)
                        .build();
                postImageRepository.save(postImage);
            } catch (IllegalArgumentException e) {
                log.error("Failed to decode image at index {}: {}", i, e.getMessage());
                throw new CreatePostFailException("Failed to decode image at index " + i);
            }
        }
    }

    private byte[] decodeBase64Image(String base64Image) {
        String imageData = base64Image.split(",")[1];
        return Base64.getDecoder().decode(imageData);
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
