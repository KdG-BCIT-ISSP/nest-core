package com.nest.core.search_service.specification;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.model.PostTag;
import com.nest.core.search_service.exception.BadRequestException;
import com.nest.core.tag_management_service.model.Tag;

import jakarta.persistence.criteria.Join;

public class PostSpecification {
    public static Specification<Post> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> {
            Join<Post, PostTag> postTagJoin = root.join("postTags");
            Join<PostTag, Tag> tagJoin = postTagJoin.join("tag");
            return criteriaBuilder.equal(tagJoin.get("name"), tag);
        };
    }

    public static Specification<Post> hasTopic(String topic) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("topic").get("name"), topic);
        };
    }

    public static Specification<Post> isArticle() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("type"), "ARTICLE");
        };
    }

    public static Specification<Post> isPost() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("type"), "USERPOST");
        };
    }

    public static Specification<Post> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<Post> hasContent(String content) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("content"), "%" + content + "%");
        };
    }

    public static Sort sortBy(String orderBy, String order) throws BadRequestException{
        if (order.equals("ASC")) {
            return Sort.by(Sort.Direction.ASC, orderBy);

        } else if (order.equals("DESC")) {
            return Sort.by(Sort.Direction.DESC, orderBy);

        } else {
            throw new BadRequestException("Order must be either ASC or DESC");
        }
    }
}
