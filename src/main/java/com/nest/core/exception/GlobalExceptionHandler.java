package com.nest.core.exception;

import com.nest.core.auth_service.controller.AuthApiController;
import com.nest.core.auth_service.exception.RefreshTokenExpiredException;
import com.nest.core.comment_management_service.exception.CreateCommentFailException;
import com.nest.core.comment_management_service.exception.DeleteCommentFailException;
import com.nest.core.comment_management_service.exception.EditCommentFailException;
import com.nest.core.comment_management_service.exception.GetCommentFailException;
import com.nest.core.member_management_service.controller.MemberApiController;
import com.nest.core.member_management_service.exception.DuplicateMemberFoundException;
import com.nest.core.member_management_service.exception.InvalidPasswordException;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.password_management_service.exception.InvalidNewPasswordException;
import com.nest.core.password_management_service.exception.InvalidResetCodeException;
import com.nest.core.post_management_service.controller.ArticleApiController;
import com.nest.core.post_management_service.exception.CreateArticleFailException;
import com.nest.core.post_management_service.exception.GetArticleFailException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = {RestController.class}, basePackageClasses = {
        AuthApiController.class, MemberApiController.class, ArticleApiController.class
})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Member_Management_Service Exception
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateMemberFoundException.class)
    public ResponseEntity<String> handleDuplicateMemberException(DuplicateMemberFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Auth_Service Exception
     */
    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<String> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Post_Management_Service Exception
     */
    @ExceptionHandler(CreateArticleFailException.class)
    public ResponseEntity<String> handleCreateArticleFailException(CreateArticleFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(GetArticleFailException.class)
    public ResponseEntity<String> handleGetArticleFailException(GetArticleFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Comment_Management_Service Exception
     */
    @ExceptionHandler(CreateCommentFailException.class)
    public ResponseEntity<String> handleCreateCommentFailException(CreateCommentFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(EditCommentFailException.class)
    public ResponseEntity<String> handleEditCommentFailException(EditCommentFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(GetCommentFailException.class)
    public ResponseEntity<String> handleGetCommentFailException(GetCommentFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(DeleteCommentFailException.class)
    public ResponseEntity<String> handleDeleteCommentFailException(DeleteCommentFailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Password_Management_Service Exception
     */

    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<String> handleInvalidResetCodeException(InvalidResetCodeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidNewPasswordException.class)
    public ResponseEntity<String> handleInvalidNewPasswordException(InvalidNewPasswordException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     * General Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred " + ex.getMessage());
    }
}
