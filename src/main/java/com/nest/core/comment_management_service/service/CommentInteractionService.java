package com.nest.core.comment_management_service.service;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.comment_management_service.model.CommentLike;
import com.nest.core.comment_management_service.model.CommentLikeId;
import com.nest.core.comment_management_service.repository.CommentLikeRepository;
import com.nest.core.comment_management_service.repository.CommentRepository;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentInteractionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;

    private String getLikeKey(Long commentId) {
        return "comment:likes:" + commentId;
    }

    private String getLikeCountKey(Long commentId) {
        return "comment:likes:count:" + commentId;
    }

    public boolean isLiked(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            log.warn("Invalid input: commentId or userId is null");
            return false;
        }
        try {
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
        } catch (Exception e) {
            log.error("Error checking isLiked for commentId: {}, userId: {}", commentId, userId, e);
            return commentLikeRepository.existsByCommentIdAndMemberId(commentId, userId);
        }
    }

    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            log.warn("Invalid input: commentId or userId is null");
            throw new IllegalArgumentException("commentId and userId must not be null");
        }
        try {
            String key = getLikeKey(commentId);
            String countKey = getLikeCountKey(commentId);
            boolean alreadyLiked = isLiked(commentId, userId);

            if (alreadyLiked) {
                commentLikeRepository.deleteByCommentIdAndMemberId(commentId, userId);
                redisTemplate.opsForSet().remove(key, userId.toString());
            } else {
                saveLike(commentId, userId);
                redisTemplate.opsForSet().add(key, userId.toString());
            }
            Long updatedCount = commentLikeRepository.countByCommentId(commentId);
            redisTemplate.opsForValue().set(countKey, updatedCount);
            return !alreadyLiked;
        } catch (Exception e) {
            log.error("Error toggling like for commentId: {}, userId: {}", commentId, userId, e);
            throw e;
        }
    }

    public Long getLikes(Long commentId) {
        if (commentId == null) {
            log.warn("Invalid input: commentId is null");
            return 0L;
        }
        try {
            String countKey = getLikeCountKey(commentId);
            Object cachedCount = redisTemplate.opsForValue().get(countKey);
            if (cachedCount instanceof Long) {
                return (Long) cachedCount;
            } else if (cachedCount instanceof Integer) {
                return ((Integer) cachedCount).longValue();
            }
            Long dbCount = commentLikeRepository.countByCommentId(commentId);
            redisTemplate.opsForValue().set(countKey, dbCount);
            return dbCount != null ? dbCount : 0L;
        } catch (Exception e) {
            log.error("Error fetching likes for commentId: {}", commentId, e);
            Long dbCount = commentLikeRepository.countByCommentId(commentId);
            return dbCount != null ? dbCount : 0L;
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncWithDatabase() {
        Set<String> keys = redisTemplate.keys("comment:likes:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3 || !"comment".equals(parts[0]) || !"likes".equals(parts[1])) {
                log.warn("Invalid Redis key format: {}", key);
                continue;
            }

            Long commentId;
            if (parts.length == 3) {
                try {
                    commentId = Long.parseLong(parts[2]);
                } catch (NumberFormatException e) {
                    log.error("Failed to parse commentId from key: {}", key, e);
                    continue;
                }
            } else if (parts.length == 4 && "count".equals(parts[2])) {
                try {
                    commentId = Long.parseLong(parts[3]);
                } catch (NumberFormatException e) {
                    log.error("Failed to parse commentId from key: {}", key, e);
                    continue;
                }
            } else {
                log.warn("Unexpected key format: {}", key);
                continue;
            }

            try {
                if (!commentRepository.existsById(commentId)) {
                    log.warn("Comment with ID {} not found, removing stale Redis keys", commentId);
                    redisTemplate.delete(getLikeKey(commentId));
                    redisTemplate.delete(getLikeCountKey(commentId));
                    continue;
                }
                if (parts.length == 3) {
                    syncLikes(commentId);
                }
            } catch (Exception e) {
                log.error("Error syncing data for key: {}", key, e);
            }
        }
    }

    @Transactional
    protected void syncLikes(Long commentId) {
        try {
            String key = getLikeKey(commentId);
            String countKey = getLikeCountKey(commentId);
            Set<Object> userIds = redisTemplate.opsForSet().members(key);
            if (userIds != null && !userIds.isEmpty()) {
                for (Object userId : userIds) {
                    saveLike(commentId, Long.valueOf(userId.toString()));
                }
                Long likes = commentLikeRepository.countByCommentId(commentId);
                commentRepository.updateLikes(commentId, likes);
                redisTemplate.opsForValue().set(countKey, likes);
            }
        } catch (Exception e) {
            log.error("Error syncing likes to DB for commentId: {}", commentId, e);
            throw e;
        }
    }

    @Transactional
    public void saveLike(Long commentId, Long memberId) {
        if (commentId == null || memberId == null || memberId == 0) {
            log.warn("Invalid input: commentId or memberId is null or invalid");
            return;
        }
        if (commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId)) {
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