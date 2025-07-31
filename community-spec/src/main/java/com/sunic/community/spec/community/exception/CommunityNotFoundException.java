package com.sunic.community.spec.community.exception;

public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }
    
    public CommunityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}