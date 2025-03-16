package com.nest.core.search_service.repository;

import com.nest.core.post_management_service.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SearchRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
}
