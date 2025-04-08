package com.nest.core.tag_management_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nest.core.tag_management_service.dto.CreateTagRequest;
import com.nest.core.tag_management_service.dto.EditTagRequest;
import com.nest.core.tag_management_service.dto.GetTagResponse;
import com.nest.core.tag_management_service.exception.CRUDFailException;
import com.nest.core.tag_management_service.exception.TagNotFoundException;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.tag_management_service.repository.TagRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;

    public List<GetTagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(GetTagResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public GetTagResponse createTag(CreateTagRequest tagRequest, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new CRUDFailException("Not Authorized to create tag");
        }
        Tag newTag = tagRequest.toEntity();
        tagRepository.save(newTag);
        return new GetTagResponse(newTag);
    }

    @Transactional
    public GetTagResponse updateTag(Long tagId, EditTagRequest editRequest, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new CRUDFailException("Not Authorized to update tag");
        }
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found"));

        tag.setName(editRequest.getName());
        tagRepository.save(tag);
        return new GetTagResponse(tag);
    }

    @Transactional
    public void deleteTag(Long tagId, String role) {
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_SUPER_ADMIN")) {
            throw new CRUDFailException("Not Authorized to delete tag");
        }
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found"));

        tagRepository.delete(tag);
    }
}
