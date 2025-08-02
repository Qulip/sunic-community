package com.sunic.community.aggregate.post.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunic.community.aggregate.post.store.CommentStore;
import com.sunic.community.aggregate.post.store.PostStore;
import com.sunic.community.spec.post.entity.Comment;
import com.sunic.community.spec.post.entity.Post;
import com.sunic.community.spec.post.facade.sdo.CommentCdo;
import com.sunic.community.spec.post.facade.sdo.CommentRdo;
import com.sunic.community.spec.post.facade.sdo.PostCdo;
import com.sunic.community.spec.post.facade.sdo.PostRdo;
import com.sunic.community.spec.post.facade.sdo.PostUdo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLogic {

	private final PostStore postStore;
	private final CommentStore commentStore;

	@Transactional
	public PostRdo createPost(PostCdo createSdo) {
		Post post = Post.create(createSdo);
		Post saved = postStore.save(post);
		return saved.toRdo();
	}

	@Transactional
	public PostRdo updatePost(PostUdo updateSdo) {
		Post post = postStore.findById(updateSdo.getId());
		post.update(updateSdo);
		Post updated = postStore.update(post);
		return updated.toRdo();
	}

	@Transactional
	public void deletePost(Integer postId) {
		postStore.deleteById(postId);
	}

	public PostRdo getPost(Integer postId) {
		Post post = postStore.findById(postId);
		return post.toRdo();
	}

	public List<PostRdo> getPostsByCommunity(Integer communityId) {
		return postStore.findByCommunityId(communityId).stream()
			.map(Post::toRdo).collect(Collectors.toList());
	}

	@Transactional
	public CommentRdo createComment(CommentCdo createSdo) {
		Comment comment = Comment.create(createSdo);
		Comment saved = commentStore.save(comment);
		return saved.toRdo();
	}

	@Transactional
	public void deleteComment(Integer commentId) {
		commentStore.deleteById(commentId);
	}

	public List<CommentRdo> getCommentsByPost(Integer postId) {
		return commentStore.findByPostId(postId).stream()
			.map(Comment::toRdo)
			.collect(Collectors.toList());
	}
}