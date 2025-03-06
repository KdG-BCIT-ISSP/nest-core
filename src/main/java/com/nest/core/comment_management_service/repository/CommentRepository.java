package com.nest.core.comment_management_service.repository;

import com.nest.core.comment_management_service.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likesCount = :likes WHERE c.id = :commentId")
    void updateLikes(@Param("commentId") Long commentId, @Param("likes") Long likes);
}
