package com.sunic.community.rest.rest.post;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunic.community.aggregate.post.logic.PostLogic;
import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.post.facade.PostFacade;
import com.sunic.community.spec.post.facade.sdo.CommentCdo;
import com.sunic.community.spec.post.facade.sdo.CommentRdo;
import com.sunic.community.spec.post.facade.sdo.PostCdo;
import com.sunic.community.spec.post.facade.sdo.PostRdo;
import com.sunic.community.spec.post.facade.sdo.PostUdo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostResource implements PostFacade {

	private final PostLogic postLogic;

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PostRdo>> getPost(@PathVariable Integer id) {
		PostRdo post = postLogic.getPost(id);
		return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
	}

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<List<PostRdo>>> getPostsByCommunity(@RequestParam Integer communityId) {
		List<PostRdo> posts = postLogic.getPostsByCommunity(communityId);
		return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<PostRdo>> createPost(@Valid @RequestBody PostCdo cdo) {
		PostRdo post = postLogic.createPost(cdo);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Post created successfully", post));
	}

	@Override
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PostRdo>> updatePost(
		@PathVariable Integer id,
		@Valid @RequestBody PostUdo udo) {
		PostRdo post = postLogic.updatePost(udo);
		return ResponseEntity.ok(ApiResponse.success("Post updated successfully", post));
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Integer id, @RequestParam Integer userId) {
		postLogic.deletePost(id, userId);
		return ResponseEntity.ok(ApiResponse.success("Post deleted successfully"));
	}

	@Override
	@PostMapping("/{id}/comments")
	public ResponseEntity<ApiResponse<CommentRdo>> createComment(
		@PathVariable Integer id,
		@Valid @RequestBody CommentCdo cdo) {
		CommentCdo updatedCdo = CommentCdo.builder()
			.content(cdo.getContent())
			.postId(id)
			.registrant(cdo.getRegistrant())
			.build();

		CommentRdo comment = postLogic.createComment(updatedCdo);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Comment created successfully", comment));
	}

	@Override
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Integer commentId,
		@RequestParam Integer userId) {
		postLogic.deleteComment(commentId, userId);
		return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
	}

	@Override
	@GetMapping("/{id}/comments")
	public ResponseEntity<ApiResponse<List<CommentRdo>>> getCommentsByPost(@PathVariable Integer id) {
		List<CommentRdo> comments = postLogic.getCommentsByPost(id);
		return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
	}
}