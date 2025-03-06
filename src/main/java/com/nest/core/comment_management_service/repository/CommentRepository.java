package com.nest.core.comment_management_service.repository;

import com.nest.core.comment_management_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
