package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    void deleteByPostIdAndMemberId(Long postId, Long memberId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
}
