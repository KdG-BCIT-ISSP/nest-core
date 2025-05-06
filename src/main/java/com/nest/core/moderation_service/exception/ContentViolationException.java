package com.nest.core.moderation_service.exception;

public class ContentViolationException extends RuntimeException {
    public ContentViolationException(String message) {
        super(message);
    }

}
