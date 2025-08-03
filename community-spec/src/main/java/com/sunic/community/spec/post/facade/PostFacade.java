package com.sunic.community.spec.post.facade;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.post.facade.sdo.CommentCdo;
import com.sunic.community.spec.post.facade.sdo.CommentRdo;
import com.sunic.community.spec.post.facade.sdo.PostCdo;
import com.sunic.community.spec.post.facade.sdo.PostRdo;
import com.sunic.community.spec.post.facade.sdo.PostUdo;

import jakarta.validation.Valid;

public interface PostFacade {

	ResponseEntity<ApiResponse<PostRdo>> getPost(Integer id);

	ResponseEntity<ApiResponse<List<PostRdo>>> getPostsByCommunity(Integer communityId);

	ResponseEntity<ApiResponse<PostRdo>> createPost(@Valid PostCdo cdo);

	ResponseEntity<ApiResponse<PostRdo>> updatePost(Integer id, @Valid PostUdo udo);

	ResponseEntity<ApiResponse<Void>> deletePost(Integer id, Integer userId);

	ResponseEntity<ApiResponse<CommentRdo>> createComment(Integer id, @Valid CommentCdo cdo);

	ResponseEntity<ApiResponse<Void>> deleteComment(Integer commentId, Integer userId);

	ResponseEntity<ApiResponse<List<CommentRdo>>> getCommentsByPost(Integer id);
}