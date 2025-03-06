package com.nest.core.comment_management_service.model;

import com.nest.core.member_management_service.model.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="commentlike")
public class CommentLike {

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    private Comment comment;

    @EmbeddedId
    private CommentLikeId id;  // To model the composite primary key
}
