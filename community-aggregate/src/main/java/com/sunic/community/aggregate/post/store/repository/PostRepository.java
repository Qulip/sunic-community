package com.sunic.community.aggregate.post.store.repository;

import com.sunic.community.aggregate.post.store.jpo.PostJpo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostJpo, Integer> {
    List<PostJpo> findByCommunityIdOrderByRegisteredTimeDesc(Integer communityId);
}