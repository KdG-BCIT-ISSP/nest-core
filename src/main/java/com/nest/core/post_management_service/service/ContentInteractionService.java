package com.nest.core.post_management_service.service;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostLike;
import com.nest.core.post_management_service.repository.PostLikeRepository;
import com.nest.core.post_management_service.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class ContentInteractionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    private String getLikeKey(Long postId) {
        return "content:likes:" + postId;
    }

    private String getViewKey(Long postId) {
        return "content:views:" + postId;
    }

    private <T> T getFromCacheOrDb(String key, java.util.function.Function<Post, T> extractor) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue != null) {
            return (T) cachedValue;
        }

        Long postId = Long.valueOf(key.split(":")[2]);
        T dbValue = postRepository.findById(postId).map(extractor).orElse(null);
        redisTemplate.opsForValue().set(key, dbValue);
        return dbValue;
    }

    public boolean isLiked(Long postId, Long userId) {
        String key = getLikeKey(postId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }

        boolean existsInDb = postLikeRepository.existsByPostIdAndMemberId(postId, userId);
        if (existsInDb) {
            redisTemplate.opsForSet().add(key, userId.toString());
        }
        return existsInDb;
    }

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        String key = getLikeKey(postId);
        boolean alreadyLiked = isLiked(postId, userId);

        if (alreadyLiked) {
            postLikeRepository.deleteByPostIdAndMemberId(postId, userId);
            redisTemplate.opsForSet().remove(key, userId.toString());

            Long size = redisTemplate.opsForSet().size(key);
            if (size == null || size == 0) {
                redisTemplate.opsForSet().add(key, "0");
            }
        } else {
            redisTemplate.opsForSet().add(key, userId.toString());
        }
        return !alreadyLiked;
    }

    public Long getViews(Long postId) {
        return getFromCacheOrDb(getViewKey(postId), Post::getViewCount);
    }

    public Long getLikes(Long postId) {
        String key = getLikeKey(postId);
        Long count = redisTemplate.opsForSet().size(key);
        if (count != null && count > 0) {
            return count;
        }
        return getFromCacheOrDb(key, Post::getLikesCount);
    }

    public void incrementView(Long postId) {
        redisTemplate.opsForValue().increment(getViewKey(postId));
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncWithDatabase() {
        Set<String> keys = redisTemplate.keys("content:*");
        if (keys.isEmpty()) return;

        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3) continue;

            Long postId = Long.parseLong(parts[2]);
            try {
                if ("likes".equals(parts[1])) {
                    syncLikes(postId);
                } else if ("views".equals(parts[1])) {
                    syncViews(postId);
                }
            } catch (Exception e) {
                System.err.println("DB Update error: " + e.getMessage());
            }
        }
    }

    @Transactional
    protected void syncLikes(Long postId) {
        Set<Object> userIds = redisTemplate.opsForSet().members(getLikeKey(postId));
        if (userIds != null) {
            for (Object userId : userIds) {
                saveLike(postId, Long.valueOf((String) userId));
            }
            Long likes = postLikeRepository.countByPostId(postId);
            postRepository.updateLikes(postId, likes);
        }
        redisTemplate.delete(getLikeKey(postId));
    }

    @Transactional
    protected void syncViews(Long postId) {
        Long views = getViews(postId);
        postRepository.updateViews(postId, views);
        redisTemplate.delete(getViewKey(postId));
    }

    @Transactional
    public void saveLike(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId) || memberId == 0) {
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
}
