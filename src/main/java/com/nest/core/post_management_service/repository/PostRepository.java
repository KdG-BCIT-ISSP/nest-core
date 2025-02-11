package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
