package com.nest.core.member_management_service.model;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.report_management_service.model.Report;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
    private String avatar = "https://cvhrma.org/wp-content/uploads/2015/07/default-profile-photo.jpg";

    private String password;

    private String region;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports;

    @PrePersist
    public void prePersist(){
        if (this.avatar == null || this.avatar.isEmpty()) {
            this.avatar = "https://cvhrma.org/wp-content/uploads/2015/07/default-profile-photo.jpg";
        }
    }

}
