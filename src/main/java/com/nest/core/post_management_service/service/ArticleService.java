package com.nest.core.post_management_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.dto.CreateArticleRequest;
import com.nest.core.post_management_service.dto.EditArticleRequest;
import com.nest.core.post_management_service.dto.EditArticleResponse;
import com.nest.core.post_management_service.dto.GetArticleResponse;
import com.nest.core.post_management_service.exception.AddBookmarkFailException;
import com.nest.core.post_management_service.exception.DeleteArticleFailException;
import com.nest.core.post_management_service.exception.EditArticleFailException;
import com.nest.core.post_management_service.exception.RemoveBookmarkFailException;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostTag;
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
    private final PostTagRepository postTagRepository;

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

        Post post = createArticleRequest.toEntity(member, topic);

        post = postRepository.save(post);

        Set<Tag> tags = createOrFindTags(createArticleRequest.getTagNames());

        Post finalPost = post;
        Set<PostTag> postTags = tags.stream()
                .map(tag -> new PostTag(finalPost, tag))
                .collect(Collectors.toSet());

        postTagRepository.saveAll(postTags);

        post.setPostTags(postTags);
        postRepository.save(post);
    }

    public List<GetArticleResponse> getArticles() {
        return postRepository.findAllArticles().stream()
                .map(GetArticleResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EditArticleResponse editArticle(EditArticleRequest editArticleRequest, Long userId) {
        if (!editArticleRequest.getMemberId().equals(userId)) {
            throw new EditArticleFailException("Not Authorized to edit this article");
        }

        Post post = postRepository.findById(editArticleRequest.getId())
                .orElseThrow(() -> new EditArticleFailException("Article Not Found"));

        post.setTitle(editArticleRequest.getTitle());
        post.setContent(editArticleRequest.getContent());
        post.setType(editArticleRequest.getType());

        JsonNode extraData = parseCoverImage(editArticleRequest.getCoverImage());
        post.setExtraData(extraData);

        Topic topic = topicRepository.findById(editArticleRequest.getTopicId())
                .orElseThrow(() -> new EditArticleFailException("Topic Not Found"));
        post.setTopic(topic);

        Set<Tag> newTags = createOrFindTags(editArticleRequest.getTagNames());

        Set<PostTag> existingTags = post.getPostTags();
        existingTags.removeIf(postTag -> !newTags.contains(postTag.getTag()));

        for (Tag tag : newTags) {
            boolean alreadyExists = existingTags.stream()
                    .anyMatch(postTag -> postTag.getTag().equals(tag));

            if (!alreadyExists) {
                existingTags.add(new PostTag(post, tag));
            }
        }

        postRepository.save(post);

        return postRepository.findById(post.getId()).map(EditArticleResponse::new).stream().toList().get(0);

    }

    public void deleteArticle(Long postId, Long userId, String userRole) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DeleteArticleFailException("Post not found"));

        if (!post.getMember().getId().equals(userId) ||
                // TODO Check which roles are allowed to delete articles
                (!userRole.equals("ROLE_ADMIN")
                && !userRole.equals("ROLE_MODERATOR")
                && !userRole.equals("ROLE_SUPER_ADMIN"))) {
            throw new DeleteArticleFailException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    public void addBookmark(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AddBookmarkFailException("Article not found"));

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new AddBookmarkFailException("Member not found"));

        if (post.getBookmarkedMembers().contains(member)) {
            throw new AddBookmarkFailException("Article already bookmarked");
        }

        member.getBookmarkedPosts().add(post);
        post.getBookmarkedMembers().add(member);
        memberRepository.save(member);
        postRepository.save(post);
    }

    public void removeBookmark(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AddBookmarkFailException("Article not found"));

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new AddBookmarkFailException("Member not found"));

        if (!post.getBookmarkedMembers().contains(member)) {
            throw new RemoveBookmarkFailException("Article was never bookmarked");
        }

        member.getBookmarkedPosts().remove(post);
        post.getBookmarkedMembers().remove(member);
        memberRepository.save(member);
        postRepository.save(post);
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

    private JsonNode parseCoverImage(String coverImage) {
        if (coverImage == null || !coverImage.contains(",")) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode imageNode = objectMapper.createObjectNode();

        String[] parts = coverImage.split(",", 2);
        imageNode.put("imageType", parts[0]);
        imageNode.put("imageData", parts[1]);

        return imageNode;
    }
}
