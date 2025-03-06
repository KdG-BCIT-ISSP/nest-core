package com.nest.core.report_management_service.model;

import java.sql.Date;

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

    @Builder.Default
    @Column(name = "comment_id")
    private Long commentId = null;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
