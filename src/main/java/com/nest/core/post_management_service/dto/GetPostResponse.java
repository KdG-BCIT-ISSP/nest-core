package com.nest.core.post_management_service.dto;

import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.post_management_service.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class GetPostResponse implements ContentResponse{
    private Long id;
    private String title;
    private String content;
    private String type;
    private Long memberId;
    private String memberUsername;
    private String memberAvatar;
    private Set<String> tagNames;
    private Set<Comment> comment;
    private List<String> imageBase64;
    private Long likesCount;
    private Long viewCount;
    private int shareCount;
    private boolean isBookmarked;
    private boolean isLiked;

    public GetPostResponse(Post post, Long userId){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.type = post.getType();
        this.memberId = post.getMember().getId();
        this.memberUsername = post.getMember().getUsername();
        this.memberAvatar = post.getMember().getAvatar().get("image").asText();
        this.tagNames = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toSet());
        this.comment = post.getComments();
        this.imageBase64 = post.getPostImages().stream()
                .map(image -> image.getImageType() + "," + Base64.getEncoder().encodeToString(image.getImageData()))
                .collect(Collectors.toList());
        this.isBookmarked = (userId != null && post.getBookmarkedMembers().stream()
                .anyMatch(member -> member.getId().equals(userId)));

        this.isLiked = (userId != null && post.getPostLikes().stream()
                .anyMatch(postLike -> postLike.getMember().getId().equals(userId)));

        this.likesCount = post.getLikesCount();
        this.viewCount = post.getViewCount();
        this.shareCount = post.getShareCount();
    }

    public GetPostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.type = post.getType();
        this.memberId = post.getMember().getId();
        this.memberUsername = post.getMember().getUsername();
        this.memberAvatar = post.getMember().getAvatar().get("image").asText();
        this.tagNames = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toSet());
        this.comment = post.getComments();
        this.imageBase64 = post.getPostImages().stream()
                .map(image -> image.getImageType() + "," + Base64.getEncoder().encodeToString(image.getImageData()))
                .collect(Collectors.toList());

        this.likesCount = post.getLikesCount();
        this.viewCount = post.getViewCount();
        this.shareCount = post.getShareCount();
    }
}
