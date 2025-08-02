package com.sunic.community.rest.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import com.sunic.community.spec.community.exception.MembershipException;
import com.sunic.community.spec.post.exception.CommentNotFoundException;
import com.sunic.community.spec.post.exception.PostNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CommunityNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleCommunityNotFound(CommunityNotFoundException ex) {
		log.error("Community not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(PostNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handlePostNotFound(PostNotFoundException ex) {
		log.error("Post not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(CommentNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleCommentNotFound(CommentNotFoundException ex) {
		log.error("Comment not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(MembershipException.class)
	public ResponseEntity<ApiResponse<Void>> handleMembershipException(MembershipException ex) {
		log.error("Membership error: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
		MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.<Map<String, String>>builder()
				.success(false)
				.message("Validation failed")
				.data(errors)
				.build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		log.error("Unexpected error occurred", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("An unexpected error occurred"));
	}
}