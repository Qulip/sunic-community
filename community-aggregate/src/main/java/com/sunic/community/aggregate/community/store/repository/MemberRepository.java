package com.sunic.community.aggregate.community.store.repository;

import com.sunic.community.aggregate.community.store.jpo.MemberJpo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberJpo, Integer> {
    Optional<MemberJpo> findByCommunityIdAndUserId(Integer communityId, Integer userId);
    boolean existsByCommunityIdAndUserId(Integer communityId, Integer userId);
    void deleteByCommunityIdAndUserId(Integer communityId, Integer userId);
}