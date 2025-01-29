package com.nest.core.tag_management_service.model;

import com.nest.core.post_management_service.model.Post;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;
}
