package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.type = 'ARTICLE'")
    Page<Post> findAllArticles(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.type = 'ARTICLE'")
    List<Post> findAllArticles();

    @Query("SELECT p FROM Post p WHERE p.type = 'USERPOST'")
    Page<Post> findAllPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.type = 'USERPOST'")
    List<Post> findAllPosts();

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.likesCount = :likes WHERE p.id = :postId")
    void updateLikes(@Param("postId") Long postId, @Param("likes") Long likes);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = :views WHERE p.id = :postId")
    void updateViews(@Param("postId") Long postId, @Param("views") Long views);

    @Query("SELECT p FROM Post p WHERE p.type = 'ARTICLE' AND p.member.id = :userId")
    List<Post> findAllArticlesByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.type = 'USERPOST' AND p.member.id = :userId")
    List<Post> findAllPostsByUserId(@Param("userId") Long userId);

}
