package com.nest.core.topic_management_service.exceptions;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(String message) {
        super(message);
    }

}
