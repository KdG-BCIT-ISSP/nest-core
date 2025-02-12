package com.nest.core.post_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageHandler {
    private String imageType;
    private byte[] imageData;
}
