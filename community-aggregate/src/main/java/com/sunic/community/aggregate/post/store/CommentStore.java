package com.sunic.community.aggregate.post.store;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sunic.community.aggregate.post.store.jpo.CommentJpo;
import com.sunic.community.aggregate.post.store.jpo.PostJpo;
import com.sunic.community.aggregate.post.store.repository.CommentRepository;
import com.sunic.community.aggregate.post.store.repository.PostRepository;
import com.sunic.community.spec.post.entity.Comment;
import com.sunic.community.spec.post.exception.CommentNotFoundException;
import com.sunic.community.spec.post.exception.PostNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentStore {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	public Comment save(Comment comment) {
		PostJpo postJpo = postRepository.findById(comment.getPostId())
			.orElseThrow(() -> new PostNotFoundException("Post not found with id: " + comment.getPostId()));

		CommentJpo jpo = CommentJpo.fromDomain(comment);
		jpo.setPost(postJpo);

		CommentJpo saved = commentRepository.save(jpo);
		return saved.toDomain();
	}

	public List<Comment> findByPostId(Integer postId) {
		return commentRepository.findByPostIdOrderByRegisteredTimeAsc(postId).stream()
			.map(CommentJpo::toDomain)
			.collect(Collectors.toList());
	}

	public void deleteById(Integer id) {
		if (!commentRepository.existsById(id)) {
			throw new CommentNotFoundException("Comment not found with id: " + id);
		}
		commentRepository.deleteById(id);
	}

}