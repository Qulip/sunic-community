package com.sunic.community.aggregate.community.store;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.jpo.CommunityTypeJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.entity.CommunityType;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityStore {
    
    private final CommunityRepository communityRepository;
    
    public Community save(Community community) {
        CommunityJpo jpo = CommunityJpo.builder()
                .type(convertToCommunityTypeJpo(community.getType()))
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
                .secretNumber(community.getSecretNumber())
                .allowSelfJoin(community.isAllowSelfJoin())
                .build();
        
        CommunityJpo saved = communityRepository.save(jpo);
        return convertToCommunity(saved);
    }
    
    public Community findById(Integer id) {
        CommunityJpo jpo = communityRepository.findById(id)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + id));
        return convertToCommunity(jpo);
    }
    
    public List<Community> findAll() {
        return communityRepository.findAll().stream()
                .map(this::convertToCommunity)
                .collect(Collectors.toList());
    }
    
    public Community update(Community community) {
        CommunityJpo jpo = communityRepository.findById(community.getId())
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + community.getId()));
        
        jpo.updateDetails(
                convertToCommunityTypeJpo(community.getType()),
                community.getThumbnail(),
                community.getName(),
                community.getDescription(),
                community.getModifier()
        );
        
        CommunityJpo saved = communityRepository.save(jpo);
        return convertToCommunity(saved);
    }
    
    public void deleteById(Integer id) {
        if (!communityRepository.existsById(id)) {
            throw new CommunityNotFoundException("Community not found with id: " + id);
        }
        communityRepository.deleteById(id);
    }
    
    public void incrementMemberCount(Integer communityId) {
        CommunityJpo jpo = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));
        jpo.incrementMemberCount();
        communityRepository.save(jpo);
    }
    
    public void decrementMemberCount(Integer communityId) {
        CommunityJpo jpo = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));
        jpo.decrementMemberCount();
        communityRepository.save(jpo);
    }
    
    private Community convertToCommunity(CommunityJpo jpo) {
        return Community.builder()
                .id(jpo.getId())
                .type(convertToCommunityType(jpo.getType()))
                .thumbnail(jpo.getThumbnail())
                .name(jpo.getName())
                .description(jpo.getDescription())
                .managerId(jpo.getManagerId())
                .managerName(jpo.getManagerName())
                .managerEmail(jpo.getManagerEmail())
                .memberCount(jpo.getMemberCount())
                .registeredTime(jpo.getRegisteredTime())
                .registrant(jpo.getRegistrant())
                .modifiedTime(jpo.getModifiedTime())
                .modifier(jpo.getModifier())
                .secretNumber(jpo.getSecretNumber())
                .allowSelfJoin(jpo.isAllowSelfJoin())
                .build();
    }
    
    private CommunityType convertToCommunityType(CommunityTypeJpo typeJpo) {
        if (typeJpo == null) return null;
        return CommunityType.valueOf(typeJpo.name());
    }
    
    private CommunityTypeJpo convertToCommunityTypeJpo(CommunityType type) {
        if (type == null) return null;
        return CommunityTypeJpo.valueOf(type.name());
    }
}