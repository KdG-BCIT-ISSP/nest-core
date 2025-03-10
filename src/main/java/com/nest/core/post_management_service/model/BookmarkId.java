package com.nest.core.post_management_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BookmarkId implements Serializable {

    private Long memberId;
    private Long postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookmarkId that = (BookmarkId) o;
        return memberId.equals(that.memberId) && postId.equals(that.postId);
    }

    @Override
    public int hashCode() {
        return 31 * memberId.hashCode() + postId.hashCode();
    }

}
