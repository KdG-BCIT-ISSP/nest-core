package com.nest.core.member_management_service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.report_management_service.model.Report;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String username;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode avatar = new ObjectMapper().createObjectNode().put("image", "");

    private String password;

    private String region;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports;

    @PostLoad
    @PrePersist
    public void presetAvatar(){
        if (this.avatar == null || this.avatar.isEmpty()) {
            this.avatar = new ObjectMapper().createObjectNode().put("image", "");
        }
    }

}
