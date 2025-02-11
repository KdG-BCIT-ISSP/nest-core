package com.nest.core.topic_management_service.repository;

import com.nest.core.topic_management_service.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
