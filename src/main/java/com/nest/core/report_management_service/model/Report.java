package com.nest.core.report_management_service.model;

import java.sql.Date;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Builder.Default
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post = null;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne
    @Builder.Default
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment = null;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
