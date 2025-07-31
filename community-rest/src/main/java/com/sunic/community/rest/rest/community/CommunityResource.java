package com.sunic.community.rest.rest.community;

import com.sunic.community.rest.config.ApiResponse;
import com.sunic.community.rest.rest.community.dto.CommunityModifyRequest;
import com.sunic.community.rest.rest.community.dto.CommunityRegisterRequest;
import com.sunic.community.rest.rest.community.dto.MemberJoinRequest;
import com.sunic.community.spec.community.facade.CommunityFacade;
import com.sunic.community.spec.community.facade.sdo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityResource {
    
    private final CommunityFacade communityFacade;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityRdo>>> getAllCommunities() {
        List<CommunityRdo> communities = communityFacade.getAllCommunities();
        return ResponseEntity.ok(ApiResponse.success("Communities retrieved successfully", communities));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(@PathVariable Integer id) {
        CommunityRdo community = communityFacade.getCommunity(id);
        return ResponseEntity.ok(ApiResponse.success("Community retrieved successfully", community));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityRdo>> registerCommunity(@Valid @RequestBody CommunityRegisterRequest request) {
        CommunityRegisterSdo sdo = CommunityRegisterSdo.builder()
                .type(request.getType())
                .thumbnail(request.getThumbnail())
                .name(request.getName())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .managerName(request.getManagerName())
                .managerEmail(request.getManagerEmail())
                .registrant(request.getRegistrant())
                .allowSelfJoin(request.isAllowSelfJoin())
                .secretNumber(request.getSecretNumber())
                .build();
        
        CommunityRdo community = communityFacade.registerCommunity(sdo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Community registered successfully", community));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityRdo>> modifyCommunity(
            @PathVariable Integer id,
            @Valid @RequestBody CommunityModifyRequest request) {
        CommunityModifySdo sdo = CommunityModifySdo.builder()
                .id(id)
                .type(request.getType())
                .thumbnail(request.getThumbnail())
                .name(request.getName())
                .description(request.getDescription())
                .modifier(request.getModifier())
                .build();
        
        CommunityRdo community = communityFacade.modifyCommunity(sdo);
        return ResponseEntity.ok(ApiResponse.success("Community modified successfully", community));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunity(@PathVariable Integer id) {
        communityFacade.deleteCommunity(id);
        return ResponseEntity.ok(ApiResponse.success("Community deleted successfully"));
    }
    
    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<Void>> joinCommunity(
            @PathVariable Integer id,
            @Valid @RequestBody MemberJoinRequest request) {
        MemberJoinSdo sdo = MemberJoinSdo.builder()
                .communityId(id)
                .userId(request.getUserId())
                .registrant(request.getRegistrant())
                .secretNumber(request.getSecretNumber())
                .build();
        
        communityFacade.joinMember(sdo);
        return ResponseEntity.ok(ApiResponse.success("Successfully joined community"));
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> leaveCommunity(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        MemberLeaveSdo sdo = MemberLeaveSdo.builder()
                .communityId(id)
                .userId(userId)
                .build();
        
        communityFacade.leaveMember(sdo);
        return ResponseEntity.ok(ApiResponse.success("Successfully left community"));
    }
    
    @GetMapping("/{id}/members/{userId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkMembership(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        boolean isMember = communityFacade.checkMembership(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Membership status retrieved", isMember));
    }
}