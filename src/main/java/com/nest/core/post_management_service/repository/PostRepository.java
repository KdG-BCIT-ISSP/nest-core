package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"tags"})
    List<Post> findAll();
}
