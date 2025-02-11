package com.nest.core.exception;

import com.nest.core.auth_service.controller.AuthApiController;
import com.nest.core.auth_service.exception.RefreshTokenExpiredException;
import com.nest.core.member_management_service.controller.MemberApiController;
import com.nest.core.member_management_service.exception.DuplicateMemberFoundException;
import com.nest.core.member_management_service.exception.InvalidPasswordException;
import com.nest.core.member_management_service.exception.MemberNotFoundException;
import com.nest.core.post_management_service.controller.ArticleApiController;
import com.nest.core.post_management_service.exception.CreateArticleFailException;
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

    /**
     * General Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred " + ex.getMessage());
    }
}
