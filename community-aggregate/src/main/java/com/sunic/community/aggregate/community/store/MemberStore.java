package com.sunic.community.aggregate.community.store;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.jpo.MemberJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.aggregate.community.store.repository.MemberRepository;
import com.sunic.community.spec.community.entity.Member;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStore {
    
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    
    public Member save(Member member) {
        CommunityJpo communityJpo = communityRepository.findById(member.getCommunityId())
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + member.getCommunityId()));
        
        MemberJpo jpo = MemberJpo.builder()
                .community(communityJpo)
                .userId(member.getUserId())
                .joinedTime(member.getJoinedTime())
                .registrant(member.getRegistrant())
                .build();
        
        MemberJpo saved = memberRepository.save(jpo);
        return convertToMember(saved);
    }
    
    public boolean existsByUserIdAndCommunityId(Integer userId, Integer communityId) {
        return memberRepository.existsByCommunityIdAndUserId(communityId, userId);
    }
    
    public void deleteByUserIdAndCommunityId(Integer userId, Integer communityId) {
        memberRepository.deleteByCommunityIdAndUserId(communityId, userId);
    }
    
    private Member convertToMember(MemberJpo jpo) {
        return Member.builder()
                .id(jpo.getId())
                .communityId(jpo.getCommunity().getId())
                .userId(jpo.getUserId())
                .joinedTime(jpo.getJoinedTime())
                .registrant(jpo.getRegistrant())
                .build();
    }
}