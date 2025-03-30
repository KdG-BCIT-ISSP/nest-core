package com.nest.core.comment_management_service.service;

import com.nest.core.comment_management_service.dto.CreateCommentRequest;
import com.nest.core.comment_management_service.dto.EditCommentRequest;
import com.nest.core.comment_management_service.dto.GetCommentResponse;
import com.nest.core.comment_management_service.exception.CreateCommentFailException;
import com.nest.core.comment_management_service.exception.EditCommentFailException;
import com.nest.core.comment_management_service.exception.GetCommentFailException;
import com.nest.core.comment_management_service.model.Comment;
import com.nest.core.comment_management_service.repository.CommentRepository;
import com.nest.core.member_management_service.model.Member;
import com.nest.core.member_management_service.repository.MemberRepository;
import com.nest.core.post_management_service.exception.DeleteArticleFailException;
import com.nest.core.post_management_service.model.Post;
import com.nest.core.post_management_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public void createComment(Long memberId, CreateCommentRequest createCommentRequest) {
        Post post = postRepository.findById(createCommentRequest.getPostId())
                .orElseThrow(() -> new CreateCommentFailException("Post not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CreateCommentFailException("Member not found"));

        Comment comment = createCommentRequest.toEntity(post, member);

        // Handle reply logic
        if (createCommentRequest.getParentId() != null) {
            Comment parentComment = commentRepository.findById(createCommentRequest.getParentId())
                    .orElseThrow(() -> new CreateCommentFailException("Parent comment not found"));

            // Enforce depth limit of 2
            if (parentComment.getParent() != null) {
                throw new CreateCommentFailException("Cannot reply to a reply. Maximum depth of 2 is allowed.");
            }

            // Link the reply to its parent
            comment.setParent(parentComment);
            parentComment.getReplies().add(comment);
            comment.setPost(parentComment.getPost()); // Ensure reply is tied to the same post
        }

        commentRepository.save(comment);
    }

    public void editComment(Long userId, EditCommentRequest editCommentRequest) {

        if(!editCommentRequest.getMemberId().equals(userId)){
            throw new EditCommentFailException("You are not allowed to edit this comment");
        }

        Comment existingComment = commentRepository.findById(editCommentRequest.getId())
                .orElseThrow(() -> new EditCommentFailException("Comment not found"));

        existingComment.setPost(postRepository.findById(editCommentRequest.getPostId())
                .orElseThrow(() -> new EditCommentFailException("Post not found")));
        existingComment.setMember(memberRepository.findById(editCommentRequest.getMemberId())
                .orElseThrow(() -> new EditCommentFailException("Member not found")));
        existingComment.setContent(editCommentRequest.getContent());
        existingComment.setEdit(true);

        commentRepository.save(existingComment);
    }

    public GetCommentResponse getComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new GetCommentFailException("Comment not found"));

        Long parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;

        return new GetCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getMember().getId(),
                comment.getMember().getAvatar(),
                comment.getMember().getUsername(),
                comment.getContent(),
                comment.getCreateAt(),
                comment.isEdit(),
                parentId);
    }

    public void deleteComment(Long userId, Long commentId, String userRole) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DeleteArticleFailException("Comment not found"));

        if (!comment.getMember().getId().equals(userId) ||
                // TODO Check which roles are allowed to delete articles
                (!userRole.equals("ROLE_ADMIN")
                        && !userRole.equals("ROLE_MODERATOR")
                        && !userRole.equals("ROLE_SUPER_ADMIN"))) {
            throw new DeleteArticleFailException("Not authorized to delete this post");
        }

        commentRepository.delete(comment);
    }

    public Page<GetCommentResponse> getComment(Long userId, Pageable pageable) {
        List<Comment> comments = commentRepository.getCommentsByMemberId(userId);

        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
        return commentPage.map(GetCommentResponse::new);
    }
}
