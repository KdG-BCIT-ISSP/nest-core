package com.nest.core.notification_service.model;

import com.nest.core.member_management_service.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(name= "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name="created_at")
    private Date createdAt;

    @Builder.Default
    @Column(name="is_read")
    private boolean isRead = false;

}
