package com.nest.core.password_management_service.exception;

public class InvalidNewPasswordException extends RuntimeException {
    public InvalidNewPasswordException(String message) {
        super(message);
    }

}
