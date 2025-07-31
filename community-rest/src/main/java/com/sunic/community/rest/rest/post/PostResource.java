package com.sunic.community.rest.rest.post;

import com.sunic.community.rest.config.ApiResponse;
import com.sunic.community.rest.rest.post.dto.CommentCreateRequest;
import com.sunic.community.rest.rest.post.dto.PostCreateRequest;
import com.sunic.community.rest.rest.post.dto.PostUpdateRequest;
import com.sunic.community.spec.post.facade.PostFacade;
import com.sunic.community.spec.post.facade.sdo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostResource {
    
    private final PostFacade postFacade;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostRdo>> getPost(@PathVariable Integer id) {
        PostRdo post = postFacade.getPost(id);
        return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostRdo>>> getPostsByCommunity(@RequestParam Integer communityId) {
        List<PostRdo> posts = postFacade.getPostsByCommunity(communityId);
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<PostRdo>> createPost(@Valid @RequestBody PostCreateRequest request) {
        PostCreateSdo sdo = PostCreateSdo.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postType(request.getPostType())
                .communityId(request.getCommunityId())
                .registrant(request.getRegistrant())
                .build();
        
        PostRdo post = postFacade.createPost(sdo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", post));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostRdo>> updatePost(
            @PathVariable Integer id,
            @Valid @RequestBody PostUpdateRequest request) {
        PostUpdateSdo sdo = PostUpdateSdo.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .postType(request.getPostType())
                .modifier(request.getModifier())
                .build();
        
        PostRdo post = postFacade.updatePost(sdo);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", post));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Integer id) {
        postFacade.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully"));
    }
    
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentRdo>> createComment(
            @PathVariable Integer id,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentCreateSdo sdo = CommentCreateSdo.builder()
                .content(request.getContent())
                .postId(id)
                .registrant(request.getRegistrant())
                .build();
        
        CommentRdo comment = postFacade.createComment(sdo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", comment));
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Integer commentId) {
        postFacade.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }
    
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentRdo>>> getCommentsByPost(@PathVariable Integer id) {
        List<CommentRdo> comments = postFacade.getCommentsByPost(id);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
}