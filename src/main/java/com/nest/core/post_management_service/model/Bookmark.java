package com.nest.core.post_management_service.model;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.member_management_service.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmark")
public class Bookmark {

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @EmbeddedId
    private BookmarkId id;

}
