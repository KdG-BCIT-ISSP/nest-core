package com.nest.core.comment_management_service.service;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.comment_management_service.model.CommentLike;
import com.nest.core.comment_management_service.model.CommentLikeId;
import com.nest.core.comment_management_service.repository.CommentLikeRepository;
import com.nest.core.comment_management_service.repository.CommentRepository;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.model.Post;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class CommentInteractionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;

    private String getLikeKey(Long postId) {
        return "comment:likes:" + postId;
    }

    private <T> T getFromCacheOrDb(String key, java.util.function.Function<Comment, T> extractor) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue != null) {
            return (T) cachedValue;
        }

        Long postId = Long.valueOf(key.split(":")[2]);
        T dbValue = commentRepository.findById(postId).map(extractor).orElse(null);
        redisTemplate.opsForValue().set(key, dbValue);
        return dbValue;
    }

    public boolean isLiked(Long commentId, Long userId) {
        String key = getLikeKey(commentId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }

        boolean existsInDb = commentLikeRepository.existsByCommentIdAndMemberId(commentId, userId);
        if (existsInDb) {
            redisTemplate.opsForSet().add(key, userId.toString());
        }
        return existsInDb;
    }

    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        String key = getLikeKey(commentId);
        boolean alreadyLiked = isLiked(commentId, userId);

        if (alreadyLiked) {
            commentLikeRepository.deleteByCommentIdAndMemberId(commentId, userId);
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

    public Long getLikes(Long postId) {
        String key = getLikeKey(postId);
        Long count = redisTemplate.opsForSet().size(key);
        if (count != null && count > 0) {
            return count;
        }
        return getFromCacheOrDb(key, Comment::getLikesCount);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncWithDatabase() {
        Set<String> keys = redisTemplate.keys("comment:*");
        if (keys.isEmpty()) return;

        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3) continue;

            Long commentId = Long.parseLong(parts[2]);
            try {
                if ("likes".equals(parts[1])) {
                    syncLikes(commentId);
                }
            } catch (Exception e) {
                System.err.println("DB Update error: " + e.getMessage());
            }
        }
    }

    @Transactional
    protected void syncLikes(Long commentId) {
        Set<Object> userIds = redisTemplate.opsForSet().members(getLikeKey(commentId));
        if (userIds != null) {
            for (Object userId : userIds) {
                saveLike(commentId, Long.valueOf((String) userId));
            }
            Long likes = commentLikeRepository.countByCommentId(commentId);
            commentRepository.updateLikes(commentId, likes);
        }
        redisTemplate.delete(getLikeKey(commentId));
    }


    @Transactional
    public void saveLike(Long commentId, Long memberId) {
        if (commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId) || memberId == 0) {
            return;
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        CommentLikeId commentLikeId = new CommentLikeId(memberId, commentId);

        CommentLike commentLike = CommentLike.builder()
                .id(commentLikeId)
                .comment(comment)
                .member(member)
                .build();

        commentLikeRepository.save(commentLike);
    }
}
