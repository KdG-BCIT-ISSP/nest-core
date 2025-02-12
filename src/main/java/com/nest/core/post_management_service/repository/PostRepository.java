package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.type = 'ARTICLE'")
    List<Post> findAllArticles();

    @Query("SELECT p FROM Post p WHERE p.type = 'USERPOST'")
    List<Post> findAllPosts();

}
