package com.nest.core.report_management_service.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
