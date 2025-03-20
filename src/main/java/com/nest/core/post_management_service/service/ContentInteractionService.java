package com.nest.core.post_management_service.service;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.dto.ContentResponse;
import com.nest.core.post_management_service.dto.GetArticleResponse;
import com.nest.core.post_management_service.dto.GetPostResponse;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostLike;
import com.nest.core.post_management_service.repository.BookmarkRepository;
import com.nest.core.post_management_service.repository.PostLikeRepository;
import com.nest.core.post_management_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.DataType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ContentInteractionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;

    private String getLikeKey(Long postId) {
        return "content:likes:" + postId;
    }

    private String getViewKey(Long postId) {
        return "content:views:" + postId;
    }

    public Long getLikes(Long postId) {
        if (postId == null) {
            log.warn("Invalid input: postId is null");
            return 0L;
        }
        try {
            String key = getLikeKey(postId);
            if (!isRedisKeySet(key)) {
                redisTemplate.delete(key);
                syncLikesFromDb(postId);
            }
            Long count = redisTemplate.opsForSet().size(key);
            if (count != null) {
                return count;
            }
            Long dbCount = postLikeRepository.countByPostId(postId);
            if (dbCount != null && dbCount > 0) {
                syncLikesFromDb(postId);
                return redisTemplate.opsForSet().size(key);
            }
            return dbCount != null ? dbCount : 0L;
        } catch (Exception e) {
            log.error("Error fetching likes for postId: {}", postId, e);
            Long dbCount = postLikeRepository.countByPostId(postId);
            return dbCount != null ? dbCount : 0L;
        }
    }

    public boolean isLiked(Long postId, Long userId) {
        if (postId == null || userId == null) {
            log.warn("Invalid input: postId or userId is null");
            return false;
        }
        try {
            String key = getLikeKey(postId);
            if (!isRedisKeySet(key)) {
                redisTemplate.delete(key);
                syncLikesFromDb(postId);
            }
            Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
            if (Boolean.TRUE.equals(isMember)) {
                return true;
            }
            boolean existsInDb = postLikeRepository.existsByPostIdAndMemberId(postId, userId);
            if (existsInDb) {
                redisTemplate.opsForSet().add(key, userId.toString());
            }
            return existsInDb;
        } catch (Exception e) {
            log.error("Error checking isLiked for postId: {}, userId: {}", postId, userId, e);
            return postLikeRepository.existsByPostIdAndMemberId(postId, userId);
        }
    }

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        if (postId == null || userId == null) {
            log.warn("Invalid input: postId or userId is null");
            throw new IllegalArgumentException("postId and userId must not be null");
        }
        try {
            String key = getLikeKey(postId);
            boolean alreadyLiked = isLiked(postId, userId);
            if (alreadyLiked) {
                postLikeRepository.deleteByPostIdAndMemberId(postId, userId);
                redisTemplate.opsForSet().remove(key, userId.toString());
            } else {
                saveLike(postId, userId);
                redisTemplate.opsForSet().add(key, userId.toString());
            }
            return !alreadyLiked;
        } catch (Exception e) {
            log.error("Error toggling like for postId: {}, userId: {}", postId, userId, e);
            throw e;
        }
    }

    private boolean isRedisKeySet(String key) {
        try {
            DataType type = redisTemplate.type(key);
            return DataType.SET.equals(type);
        } catch (Exception e) {
            log.warn("Error checking Redis key type for key: {}", key, e);
            return false;
        }
    }

    private void syncLikesFromDb(Long postId) {
        try {
            List<PostLike> likes = postLikeRepository.findByPostId(postId);
            if (likes != null && !likes.isEmpty()) {
                String key = getLikeKey(postId);
                for (PostLike like : likes) {
                    redisTemplate.opsForSet().add(key, like.getMember().getId().toString());
                }
            }
        } catch (Exception e) {
            log.error("Error syncing likes from DB for postId: {}", postId, e);
        }
    }

    public Long getViews(Long postId) {
        if (postId == null) {
            log.warn("Invalid input: postId is null");
            return 0L;
        }
        try {
            Object cachedValue = redisTemplate.opsForValue().get(getViewKey(postId));
            if (cachedValue instanceof Integer) {
                return ((Integer) cachedValue).longValue();
            } else if (cachedValue instanceof Long) {
                return (Long) cachedValue;
            }
            Long dbValue = postRepository.findById(postId)
                    .map(Post::getViewCount)
                    .orElse(0L);
            redisTemplate.opsForValue().set(getViewKey(postId), dbValue);
            return dbValue;
        } catch (Exception e) {
            log.error("Error fetching views for postId: {}", postId, e);
            return postRepository.findById(postId).map(Post::getViewCount).orElse(0L);
        }
    }

    public void incrementView(Long postId) {
        if (postId == null) {
            log.warn("Invalid input: postId is null");
            return;
        }
        try {
            redisTemplate.opsForValue().increment(getViewKey(postId));
        } catch (Exception e) {
            log.error("Error incrementing view for postId: {}", postId, e);
        }
    }

    @Transactional
    protected void syncLikes(Long postId) {
        try {
            Set<Object> userIds = redisTemplate.opsForSet().members(getLikeKey(postId));
            if (userIds != null && !userIds.isEmpty()) {
                for (Object userId : userIds) {
                    Long memberId = Long.valueOf(userId.toString());
                    saveLike(postId, memberId);
                }
                Long likes = postLikeRepository.countByPostId(postId);
                postRepository.updateLikes(postId, likes);
            }
        } catch (Exception e) {
            log.error("Error syncing likes to DB for postId: {}", postId, e);
        }
    }

    @Transactional
    public void saveLike(Long postId, Long memberId) {
        if (postId == null || memberId == null || memberId == 0) {
            log.warn("Invalid input: postId or memberId is null or invalid");
            return;
        }
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            return;
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        postLikeRepository.save(postLike);
    }

    public List<GetArticleResponse> getAllBookmarkedArticle(Long userId) {
        if (userId == null) {
            log.warn("Invalid input: userId is null");
            return List.of();
        }
        return bookmarkRepository.findBookmarkedArticlesByMemberId(userId).stream()
                .map(post -> new GetArticleResponse(post, userId))
                .collect(Collectors.toList());
    }

    public List<GetPostResponse> getAllBookmarkedPost(Long userId) {
        if (userId == null) {
            log.warn("Invalid input: userId is null");
            return List.of();
        }
        return bookmarkRepository.findBookmarkedPostsByMemberId(userId).stream()
                .map(post -> new GetPostResponse(post, userId))
                .collect(Collectors.toList());
    }

    public ContentResponse getContent(Long contentId) {
        if (contentId == null) {
            log.warn("Invalid input: contentId is null");
            throw new IllegalArgumentException("contentId must not be null");
        }
        Post post = postRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        if (Objects.equals(post.getType(), "ARTICLE")) {
            return new GetArticleResponse(post);
        } else if (Objects.equals(post.getType(), "USERPOST")) {
            return new GetPostResponse(post);
        } else {
            throw new RuntimeException("Unknown content type");
        }
    }
}