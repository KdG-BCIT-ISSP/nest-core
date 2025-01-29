package com.nest.core.topic_management_service.model;


import com.nest.core.post_management_service.model.Post;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;
}
