package com.sunic.community.aggregate.community.store.repository;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityJpo, Integer> {
}