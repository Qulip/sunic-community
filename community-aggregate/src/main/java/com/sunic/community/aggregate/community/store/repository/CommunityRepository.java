package com.sunic.community.aggregate.community.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityJpo, Integer> {
}