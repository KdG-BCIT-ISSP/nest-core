package com.nest.core.post_management_service.model;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.report_management_service.model.Report;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.topic_management_service.model.Topic;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports;

    @ManyToMany
    @JoinTable(
            name = "posttag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;
}
