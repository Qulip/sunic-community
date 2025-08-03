package com.sunic.community.spec.community.facade;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.community.facade.sdo.CommunityCdo;
import com.sunic.community.spec.community.facade.sdo.CommunityRdo;
import com.sunic.community.spec.community.facade.sdo.CommunityUdo;
import com.sunic.community.spec.community.facade.sdo.MemberJoinCdo;

import jakarta.validation.Valid;

public interface CommunityFacade {

	ResponseEntity<ApiResponse<List<CommunityRdo>>> getAllCommunities();

	ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(Integer id);

	ResponseEntity<ApiResponse<CommunityRdo>> registerCommunity(@Valid CommunityCdo cdo);

	ResponseEntity<ApiResponse<CommunityRdo>> modifyCommunity(Integer id, @Valid CommunityUdo udo);

	ResponseEntity<ApiResponse<Void>> deleteCommunity(Integer id, Integer userId);

	ResponseEntity<ApiResponse<Void>> joinCommunity(Integer id, @Valid MemberJoinCdo cdo);

	ResponseEntity<ApiResponse<Void>> leaveCommunity(Integer id, Integer userId);

	ResponseEntity<ApiResponse<Boolean>> checkMembership(Integer id, Integer userId);
}