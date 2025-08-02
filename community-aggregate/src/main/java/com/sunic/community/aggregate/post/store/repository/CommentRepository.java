package com.sunic.community.aggregate.post.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunic.community.aggregate.post.store.jpo.CommentJpo;

@Repository
public interface CommentRepository extends JpaRepository<CommentJpo, Integer> {
	List<CommentJpo> findByPostIdOrderByRegisteredTimeAsc(Integer postId);
}