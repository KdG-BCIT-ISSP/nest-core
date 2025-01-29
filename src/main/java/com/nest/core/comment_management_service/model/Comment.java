package com.nest.core.comment_management_service.model;

import com.nest.core.member_management_service.model.Member;
import com.nest.core.post_management_service.model.Post;

import jakarta.persistence.*;


@Entity
@Table(name="comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;



}
