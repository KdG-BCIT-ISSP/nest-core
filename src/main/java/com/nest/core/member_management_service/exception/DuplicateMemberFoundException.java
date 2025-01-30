package com.nest.core.member_management_service.exception;

public class DuplicateMemberFoundException extends RuntimeException {
    public DuplicateMemberFoundException(String message){
        super(message);
    }
}
