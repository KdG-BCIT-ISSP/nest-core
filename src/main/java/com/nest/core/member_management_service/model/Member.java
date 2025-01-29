package com.nest.core.member_management_service.model;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.report_management_service.model.Report;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String role;

    private String username;

    private String avatar;

    private String password;

    private String region;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports;



}
