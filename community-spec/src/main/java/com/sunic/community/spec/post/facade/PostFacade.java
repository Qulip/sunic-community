package com.sunic.community.spec.post.facade;

import com.sunic.community.spec.post.facade.sdo.*;

import java.util.List;

public interface PostFacade {
    
    PostRdo createPost(PostCreateSdo createSdo);
    
    PostRdo updatePost(PostUpdateSdo updateSdo);
    
    void deletePost(Integer postId);
    
    PostRdo getPost(Integer postId);
    
    List<PostRdo> getPostsByCommunity(Integer communityId);
    
    CommentRdo createComment(CommentCreateSdo createSdo);
    
    void deleteComment(Integer commentId);
    
    List<CommentRdo> getCommentsByPost(Integer postId);
}