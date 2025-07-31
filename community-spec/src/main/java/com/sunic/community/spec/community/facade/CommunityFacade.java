package com.sunic.community.spec.community.facade;

import com.sunic.community.spec.community.facade.sdo.*;

import java.util.List;

public interface CommunityFacade {
    
    CommunityRdo registerCommunity(CommunityRegisterSdo registerSdo);
    
    CommunityRdo modifyCommunity(CommunityModifySdo modifySdo);
    
    void deleteCommunity(Integer communityId);
    
    CommunityRdo getCommunity(Integer communityId);
    
    List<CommunityRdo> getAllCommunities();
    
    void joinMember(MemberJoinSdo joinSdo);
    
    void leaveMember(MemberLeaveSdo leaveSdo);
    
    boolean checkMembership(Integer communityId, Integer userId);
}