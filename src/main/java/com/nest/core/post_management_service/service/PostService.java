package com.nest.core.post_management_service.service;

import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.dto.*;
import com.nest.core.post_management_service.exception.*;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostImage;
import com.nest.core.post_management_service.model.PostTag;
import com.nest.core.post_management_service.repository.PostImageRepository;
import com.nest.core.post_management_service.repository.PostRepository;
import com.nest.core.post_management_service.repository.PostTagRepository;
import com.nest.core.search_service.repository.SearchRepository;
import com.nest.core.search_service.specification.PostSpecification;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.tag_management_service.repository.TagRepository;
import com.nest.core.topic_management_service.model.Topic;
import com.nest.core.topic_management_service.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private final SearchRepository searchRepository;

    public void createPost(CreatePostRequest createPostRequest, Long userId) {
        Member member = findMemberById(userId);
        Topic topic = findTopicById(createPostRequest.getTopicId());
        Post createdPost = createAndSavePost(createPostRequest, member, topic);
        Set<Tag> tags = createOrFindTags(createPostRequest.getTagNames());
        savePostTags(createdPost, tags);
        saveImages(createPostRequest, createdPost);
    }

    public List<GetPostResponse> getPosts(Long userId) {
        return postRepository.findAllPosts().stream()
                .map(post -> new GetPostResponse(post, userId))
                .collect(Collectors.toList());
    }
    @Transactional
    public EditPostResponse editPost(EditPostRequest editPostRequest, Long userId) {
        if (!editPostRequest.getMemberId().equals(userId)) {
            throw new EditPostFailException("Not Authorized to edit this article");
        }

        Post post = postRepository.findById(editPostRequest.getId())
                .orElseThrow(() -> new EditPostFailException("Article Not Found"));

        post.setTitle(editPostRequest.getTitle());
        post.setContent(editPostRequest.getContent());
        post.setType(editPostRequest.getType());

        Topic topic = topicRepository.findById(editPostRequest.getTopicId())
                .orElseThrow(() -> new EditPostFailException("Topic Not Found"));
        post.setTopic(topic);

        Set<Tag> newTags = createOrFindTags(editPostRequest.getTagNames());

        Set<PostTag> existingTags = post.getPostTags();
        existingTags.removeIf(postTag -> !newTags.contains(postTag.getTag()));

        for (Tag tag : newTags) {
            boolean alreadyExists = existingTags.stream()
                    .anyMatch(postTag -> postTag.getTag().equals(tag));

            if (!alreadyExists) {
                existingTags.add(new PostTag(post, tag));
            }
        }

        editImages(editPostRequest, post);

        postRepository.save(post);

        return postRepository.findById(post.getId()).map(EditPostResponse::new).stream().toList().get(0);

    }

    @Transactional
    public void deletePost(Long postId, Long userId, String userRole) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DeletePostFailException("Post not found"));

        if (!post.getMember().getId().equals(userId) &&
                (!userRole.equals("ROLE_ADMIN")
                        && !userRole.equals("ROLE_MODERATOR")
                        && !userRole.equals("ROLE_SUPER_ADMIN"))) {
            throw new DeleteArticleFailException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    /**
     * Helper methods
     */

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
                ImageHandler imageData = decodeBase64Image(createPostRequest.getImageBase64().get(i));
                PostImage postImage = PostImage.builder()
                        .post(createdPost)
                        .imageData(imageData.getImageData())
                        .imageType(imageData.getImageType())
                        .build();
                postImageRepository.save(postImage);
            } catch (IllegalArgumentException e) {
                log.error("Failed to decode image at index {}: {}", i, e.getMessage());
                throw new CreatePostFailException("Failed to decode image at index " + i);
            }
        }
    }

    private void editImages(EditPostRequest editPostRequest, Post post) {
        postImageRepository.deleteAll(post.getPostImages());
        post.getPostImages().clear();

        if (editPostRequest.getImageBase64() == null || editPostRequest.getImageBase64().isEmpty()) {
            return;
        }

        List<PostImage> newImages = new ArrayList<>();
        for (String base64Image : editPostRequest.getImageBase64()) {
            try {
                ImageHandler imageData = decodeBase64Image(base64Image);
                newImages.add(PostImage.builder()
                        .post(post)
                        .imageData(imageData.getImageData())
                        .imageType(imageData.getImageType())
                        .build());
            } catch (IllegalArgumentException e) {
                log.error("Failed to decode image: {}", e.getMessage());
                throw new EditPostFailException("Failed to decode image");
            }
        }

        postImageRepository.saveAll(newImages);
        post.getPostImages().addAll(newImages);
    }


    private ImageHandler decodeBase64Image(String base64Image) {
        String imageType = base64Image.split(",")[0];
        String imageData = base64Image.split(",")[1];
        return new ImageHandler(imageType, Base64.getDecoder().decode(imageData));
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

    public List<GetPostResponse> getPostsByUserId(Long userId){
        return postRepository.findAllPostsByUserId(userId).stream().map(GetPostResponse::new).collect(Collectors.toList());
    }
}
