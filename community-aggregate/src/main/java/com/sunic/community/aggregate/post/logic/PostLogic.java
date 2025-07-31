package com.sunic.community.aggregate.post.logic;

import com.sunic.community.aggregate.post.store.CommentStore;
import com.sunic.community.aggregate.post.store.PostStore;
import com.sunic.community.spec.post.entity.Comment;
import com.sunic.community.spec.post.entity.Post;
import com.sunic.community.spec.post.facade.PostFacade;
import com.sunic.community.spec.post.facade.sdo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLogic implements PostFacade {
    
    private final PostStore postStore;
    private final CommentStore commentStore;
    
    @Override
    @Transactional
    public PostRdo createPost(PostCreateSdo createSdo) {
        Post post = Post.create(createSdo);
        Post saved = postStore.save(post);
        return convertToPostRdo(saved);
    }
    
    @Override
    @Transactional
    public PostRdo updatePost(PostUpdateSdo updateSdo) {
        Post existing = postStore.findById(updateSdo.getId());
        Post updated = existing.update(updateSdo);
        Post saved = postStore.update(updated);
        return convertToPostRdo(saved);
    }
    
    @Override
    @Transactional
    public void deletePost(Integer postId) {
        postStore.deleteById(postId);
    }
    
    @Override
    public PostRdo getPost(Integer postId) {
        Post post = postStore.findById(postId);
        List<Comment> comments = commentStore.findByPostId(postId);
        
        List<CommentRdo> commentRdos = comments.stream()
                .map(this::convertToCommentRdo)
                .collect(Collectors.toList());
        
        PostRdo postRdo = convertToPostRdo(post);
        return PostRdo.builder()
                .id(postRdo.getId())
                .title(postRdo.getTitle())
                .content(postRdo.getContent())
                .postType(postRdo.getPostType())
                .communityId(postRdo.getCommunityId())
                .registeredTime(postRdo.getRegisteredTime())
                .registrant(postRdo.getRegistrant())
                .modifiedTime(postRdo.getModifiedTime())
                .modifier(postRdo.getModifier())
                .comments(commentRdos)
                .build();
    }
    
    @Override
    public List<PostRdo> getPostsByCommunity(Integer communityId) {
        return postStore.findByCommunityId(communityId).stream()
                .map(this::convertToPostRdo)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CommentRdo createComment(CommentCreateSdo createSdo) {
        Comment comment = Comment.create(createSdo);
        Comment saved = commentStore.save(comment);
        return convertToCommentRdo(saved);
    }
    
    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        commentStore.deleteById(commentId);
    }
    
    @Override
    public List<CommentRdo> getCommentsByPost(Integer postId) {
        return commentStore.findByPostId(postId).stream()
                .map(this::convertToCommentRdo)
                .collect(Collectors.toList());
    }
    
    private PostRdo convertToPostRdo(Post post) {
        return PostRdo.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .communityId(post.getCommunityId())
                .registeredTime(post.getRegisteredTime())
                .registrant(post.getRegistrant())
                .modifiedTime(post.getModifiedTime())
                .modifier(post.getModifier())
                .build();
    }
    
    private CommentRdo convertToCommentRdo(Comment comment) {
        return CommentRdo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPostId())
                .registeredTime(comment.getRegisteredTime())
                .registrant(comment.getRegistrant())
                .modifiedTime(comment.getModifiedTime())
                .modifier(comment.getModifier())
                .build();
    }
}