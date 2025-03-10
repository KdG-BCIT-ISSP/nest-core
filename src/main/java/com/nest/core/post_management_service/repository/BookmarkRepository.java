package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.Bookmark;
import com.nest.core.post_management_service.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b.post FROM Bookmark b WHERE b.member.id = :memberId AND b.post.type = 'ARTICLE'")
    List<Post> findBookmarkedArticlesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT b.post FROM Bookmark b WHERE b.member.id = :memberId AND b.post.type = 'USERPOST'")
    List<Post> findBookmarkedPostsByMemberId(@Param("memberId") Long memberId);

}
