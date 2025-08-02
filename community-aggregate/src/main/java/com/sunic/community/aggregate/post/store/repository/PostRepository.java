package com.sunic.community.aggregate.post.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunic.community.aggregate.post.store.jpo.PostJpo;

@Repository
public interface PostRepository extends JpaRepository<PostJpo, Integer> {
	List<PostJpo> findByCommunityIdOrderByRegisteredTimeDesc(Integer communityId);
}