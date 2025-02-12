package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
