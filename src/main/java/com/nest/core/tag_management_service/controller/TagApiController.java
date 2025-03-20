package com.nest.core.tag_management_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nest.core.auth_service.dto.CustomSecurityUserDetails;
import com.nest.core.tag_management_service.dto.CreateTagRequest;
import com.nest.core.tag_management_service.dto.EditTagRequest;
import com.nest.core.tag_management_service.exception.CRUDFailException;
import com.nest.core.tag_management_service.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
public class TagApiController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<?> getTags() {
        try {
            return ResponseEntity.ok(tagService.getAllTags());
        } catch(Exception e) {
            throw new CRUDFailException("Failed to get tags: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTag(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateTagRequest tagRequest) {
        if (userDetails instanceof CustomSecurityUserDetails) {
            try {
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                return ResponseEntity.ok(tagService.createTag(tagRequest, role));

            } catch (Exception e) {
                throw new CRUDFailException("Failed to create tag: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @PutMapping("/update/{tagId}")
    public ResponseEntity<?> updateTag(@AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long tagId,
        @RequestBody EditTagRequest editRequest) {

        if (userDetails instanceof CustomSecurityUserDetails) {
            try {
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                return ResponseEntity.ok(tagService.updateTag(tagId, editRequest, role));

            } catch (Exception e) {
                throw new CRUDFailException("Failed to update tag: " + e.getMessage());
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }

    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<?> deleteTag(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long tagId) {
        if (userDetails instanceof CustomSecurityUserDetails) {
            try {
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                tagService.deleteTag(tagId, role);
                return ResponseEntity.ok("Tag deleted");

            } catch (Exception e) {
                throw new CRUDFailException("Failed to delete tag: " + e.getMessage());
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
        }
    }
}
