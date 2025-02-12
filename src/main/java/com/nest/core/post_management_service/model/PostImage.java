package com.nest.core.post_management_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "postimage")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_type", nullable = false)
    private String imageType;

    @Column(name = "image_data", columnDefinition = "BYTEA", nullable = false)
    private byte[] imageData; // Store image as byte array
}
