package com.nest.core.comment_management_service.repository;

import com.nest.core.comment_management_service.dto.GetCommentResponse;
import com.nest.core.comment_management_service.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.likesCount = :likes WHERE c.id = :commentId")
    void updateLikes(@Param("commentId") Long commentId, @Param("likes") Long likes);


    @Query("SELECT c FROM Comment c WHERE c.member.id = :userId")
    List<Comment> getCommentsByMemberId(@Param("userId") Long userId);
}
