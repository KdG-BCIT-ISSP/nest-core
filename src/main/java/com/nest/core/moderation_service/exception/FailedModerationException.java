package com.nest.core.moderation_service.exception;

public class FailedModerationException extends RuntimeException {
    public FailedModerationException(String message) {
        super(message);
    }

}
