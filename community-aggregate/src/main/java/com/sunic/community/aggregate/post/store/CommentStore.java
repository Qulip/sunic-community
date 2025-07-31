package com.sunic.community.aggregate.post.store;

import com.sunic.community.aggregate.post.store.jpo.CommentJpo;
import com.sunic.community.aggregate.post.store.jpo.PostJpo;
import com.sunic.community.aggregate.post.store.repository.CommentRepository;
import com.sunic.community.aggregate.post.store.repository.PostRepository;
import com.sunic.community.spec.post.entity.Comment;
import com.sunic.community.spec.post.exception.CommentNotFoundException;
import com.sunic.community.spec.post.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentStore {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    
    public Comment save(Comment comment) {
        PostJpo postJpo = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + comment.getPostId()));
        
        CommentJpo jpo = CommentJpo.builder()
                .content(comment.getContent())
                .post(postJpo)
                .registeredTime(comment.getRegisteredTime())
                .registrant(comment.getRegistrant())
                .modifiedTime(comment.getModifiedTime())
                .modifier(comment.getModifier())
                .build();
        
        CommentJpo saved = commentRepository.save(jpo);
        return convertToComment(saved);
    }
    
    public List<Comment> findByPostId(Integer postId) {
        return commentRepository.findByPostIdOrderByRegisteredTimeAsc(postId).stream()
                .map(this::convertToComment)
                .collect(Collectors.toList());
    }
    
    public void deleteById(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
    
    private Comment convertToComment(CommentJpo jpo) {
        return Comment.builder()
                .id(jpo.getId())
                .content(jpo.getContent())
                .postId(jpo.getPost().getId())
                .registeredTime(jpo.getRegisteredTime())
                .registrant(jpo.getRegistrant())
                .modifiedTime(jpo.getModifiedTime())
                .modifier(jpo.getModifier())
                .build();
    }
}