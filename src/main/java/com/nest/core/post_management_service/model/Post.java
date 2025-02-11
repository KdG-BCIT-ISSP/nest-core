package com.nest.core.post_management_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.tag_management_service.model.Tag;
import com.nest.core.topic_management_service.model.Topic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private Topic topic;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "posttag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    private String type;

    @Column(name="likes_count")
    private int likesCount;

    @Column(name="view_count")
    private int viewCount;

    @Column(name="share_count")
    private int shareCount;

    @Column(name = "extra_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode extraData;
}
