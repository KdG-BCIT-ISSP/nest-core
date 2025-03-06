package com.nest.core.comment_management_service.repository;

import com.nest.core.comment_management_service.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
    void deleteByCommentIdAndMemberId(Long commentId, Long memberId);

    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.id.commentId = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);
}
