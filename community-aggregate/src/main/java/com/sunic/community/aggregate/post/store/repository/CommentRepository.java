package com.sunic.community.aggregate.post.store.repository;

import com.sunic.community.aggregate.post.store.jpo.CommentJpo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentJpo, Integer> {
    List<CommentJpo> findByPostIdOrderByRegisteredTimeAsc(Integer postId);
}