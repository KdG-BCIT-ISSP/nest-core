package com.nest.core.topic_management_service.service;

import com.nest.core.topic_management_service.repository.TopicRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nest.core.topic_management_service.dto.CreateTopicRequest;
import com.nest.core.topic_management_service.dto.EditTopicRequest;
import com.nest.core.topic_management_service.dto.GetTopicResponse;
import com.nest.core.topic_management_service.exceptions.TopicCRUDFailException;
import com.nest.core.topic_management_service.exceptions.TopicNotFoundException;
import com.nest.core.topic_management_service.model.Topic;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final TopicRepository topicRepository;

    public List<GetTopicResponse> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(GetTopicResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public GetTopicResponse createTopic(CreateTopicRequest createRequest, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new TopicCRUDFailException("Not Authorized to create topic");
        }
        Topic newTopic = createRequest.toEntity();
        topicRepository.save(newTopic);
        return new GetTopicResponse(newTopic);
    }

    @Transactional
    public GetTopicResponse updateTopic(Long topicId, EditTopicRequest editRequest, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new TopicCRUDFailException("Not Authorized to update topic");
        }
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Topic not found"));

        topic.setName(editRequest.getName());
        topic.setDescription(editRequest.getDescription());
        topicRepository.save(topic);
        return new GetTopicResponse(topic);
    }

    @Transactional
    public void deleteTopic(Long topicId, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new TopicCRUDFailException("Not Authorized to delete topic");
        }
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Topic not found"));
        topicRepository.delete(topic);
    }
}
