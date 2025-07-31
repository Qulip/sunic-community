package com.sunic.community.aggregate.community.logic;

import com.sunic.community.aggregate.community.store.CommunityStore;
import com.sunic.community.aggregate.community.store.MemberStore;
import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.entity.Member;
import com.sunic.community.spec.community.exception.MembershipException;
import com.sunic.community.spec.community.facade.CommunityFacade;
import com.sunic.community.spec.community.facade.sdo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityLogic implements CommunityFacade {
    
    private final CommunityStore communityStore;
    private final MemberStore memberStore;
    
    @Override
    @Transactional
    public CommunityRdo registerCommunity(CommunityRegisterSdo registerSdo) {
        Community community = Community.create(registerSdo);
        Community saved = communityStore.save(community);
        return convertToCommunityRdo(saved);
    }
    
    @Override
    @Transactional
    public CommunityRdo modifyCommunity(CommunityModifySdo modifySdo) {
        Community existing = communityStore.findById(modifySdo.getId());
        Community updated = existing.modify(modifySdo);
        Community saved = communityStore.update(updated);
        return convertToCommunityRdo(saved);
    }
    
    @Override
    @Transactional
    public void deleteCommunity(Integer communityId) {
        communityStore.deleteById(communityId);
    }
    
    @Override
    public CommunityRdo getCommunity(Integer communityId) {
        Community community = communityStore.findById(communityId);
        return convertToCommunityRdo(community);
    }
    
    @Override
    public List<CommunityRdo> getAllCommunities() {
        return communityStore.findAll().stream()
                .map(this::convertToCommunityRdo)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void joinMember(MemberJoinSdo joinSdo) {
        Community community = communityStore.findById(joinSdo.getCommunityId());
        
        if (memberStore.existsByUserIdAndCommunityId(joinSdo.getUserId(), joinSdo.getCommunityId())) {
            throw new MembershipException("User is already a member of this community");
        }
        
        if (!community.isAllowSelfJoin() && !community.getSecretNumber().equals(joinSdo.getSecretNumber())) {
            throw new MembershipException("Invalid secret number for community");
        }
        
        Member member = Member.create(joinSdo);
        memberStore.save(member);
        
        Community updatedCommunity = community.addMember();
        communityStore.update(updatedCommunity);
    }
    
    @Override
    @Transactional
    public void leaveMember(MemberLeaveSdo leaveSdo) {
        if (!memberStore.existsByUserIdAndCommunityId(leaveSdo.getUserId(), leaveSdo.getCommunityId())) {
            throw new MembershipException("User is not a member of this community");
        }
        
        memberStore.deleteByUserIdAndCommunityId(leaveSdo.getUserId(), leaveSdo.getCommunityId());
        
        Community community = communityStore.findById(leaveSdo.getCommunityId());
        Community updatedCommunity = community.removeMember();
        communityStore.update(updatedCommunity);
    }
    
    @Override
    public boolean checkMembership(Integer communityId, Integer userId) {
        return memberStore.existsByUserIdAndCommunityId(userId, communityId);
    }
    
    private CommunityRdo convertToCommunityRdo(Community community) {
        return CommunityRdo.builder()
                .id(community.getId())
                .type(community.getType())
                .thumbnail(community.getThumbnail())
                .name(community.getName())
                .description(community.getDescription())
                .managerId(community.getManagerId())
                .managerName(community.getManagerName())
                .managerEmail(community.getManagerEmail())
                .memberCount(community.getMemberCount())
                .registeredTime(community.getRegisteredTime())
                .registrant(community.getRegistrant())
                .modifiedTime(community.getModifiedTime())
                .modifier(community.getModifier())
                .allowSelfJoin(community.isAllowSelfJoin())
                .build();
    }
}