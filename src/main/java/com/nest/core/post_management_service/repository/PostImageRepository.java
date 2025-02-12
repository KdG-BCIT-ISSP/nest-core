package com.nest.core.post_management_service.repository;

import com.nest.core.post_management_service.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
