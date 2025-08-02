package com.sunic.community.aggregate.post.store;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.aggregate.post.store.jpo.PostJpo;
import com.sunic.community.aggregate.post.store.repository.PostRepository;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import com.sunic.community.spec.post.entity.Post;
import com.sunic.community.spec.post.exception.PostNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostStore {

	private final PostRepository postRepository;
	private final CommunityRepository communityRepository;

	public Post save(Post post) {
		CommunityJpo communityJpo = communityRepository.findById(post.getCommunityId())
			.orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + post.getCommunityId()));

		PostJpo jpo = PostJpo.fromDomain(post);
		jpo.setCommunity(communityJpo);

		PostJpo saved = postRepository.save(jpo);
		return saved.toDomain();
	}

	public Post findById(Integer id) {
		PostJpo jpo = postRepository.findById(id)
			.orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
		return jpo.toDomain();
	}

	public List<Post> findByCommunityId(Integer communityId) {
		return postRepository.findByCommunityIdOrderByRegisteredTimeDesc(communityId).stream()
			.map(PostJpo::toDomain)
			.collect(Collectors.toList());
	}

	public Post update(Post post) {
		PostJpo jpo = postRepository.findById(post.getId())
			.orElseThrow(() -> new PostNotFoundException("Post not found with id: " + post.getId()));

		jpo.updateFromDomain(post);

		PostJpo saved = postRepository.save(jpo);
		return saved.toDomain();
	}

	public void deleteById(Integer id) {
		if (!postRepository.existsById(id)) {
			throw new PostNotFoundException("Post not found with id: " + id);
		}
		postRepository.deleteById(id);
	}

}