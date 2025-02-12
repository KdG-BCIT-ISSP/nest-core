package com.nest.core.post_management_service.model;

import com.nest.core.tag_management_service.model.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posttag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "posttag_post_fk"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false, foreignKey = @ForeignKey(name = "posttag_tag_fk"))
    private Tag tag;

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}