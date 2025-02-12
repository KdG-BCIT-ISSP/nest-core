package com.nest.core.post_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditPostRequest {
    private Long id;
    private Long memberId;
    private String title;
    private String content;
    private String type;
    private Long topicId;
    private Set<String> tagNames;
    private List<String> imageBase64;
}
