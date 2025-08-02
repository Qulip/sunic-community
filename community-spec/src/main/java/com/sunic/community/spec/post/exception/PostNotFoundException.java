package com.sunic.community.spec.post.exception;

public class PostNotFoundException extends RuntimeException {
	public PostNotFoundException(String message) {
		super(message);
	}

	public PostNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}