package com.sunic.community.rest.rest.community;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunic.community.aggregate.community.logic.CommunityLogic;
import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.community.facade.CommunityFacade;
import com.sunic.community.spec.community.facade.sdo.CommunityCdo;
import com.sunic.community.spec.community.facade.sdo.CommunityRdo;
import com.sunic.community.spec.community.facade.sdo.CommunityUdo;
import com.sunic.community.spec.community.facade.sdo.MemberJoinCdo;
import com.sunic.community.spec.community.facade.sdo.MemberLeaveCdo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityResource implements CommunityFacade {

	private final CommunityLogic communityLogic;

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<List<CommunityRdo>>> getAllCommunities() {
		List<CommunityRdo> communities = communityLogic.getAllCommunities();
		return ResponseEntity.ok(ApiResponse.success("Communities retrieved successfully", communities));
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(@PathVariable Integer id) {
		CommunityRdo community = communityLogic.getCommunity(id);
		return ResponseEntity.ok(ApiResponse.success("Community retrieved successfully", community));
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<CommunityRdo>> registerCommunity(@Valid @RequestBody CommunityCdo cdo) {
		CommunityRdo community = communityLogic.registerCommunity(cdo);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Community registered successfully", community));
	}

	@Override
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CommunityRdo>> modifyCommunity(
		@PathVariable Integer id,
		@Valid @RequestBody CommunityUdo udo) {
		CommunityUdo updatedUdo = CommunityUdo.builder()
			.id(id)
			.type(udo.getType())
			.thumbnail(udo.getThumbnail())
			.name(udo.getName())
			.description(udo.getDescription())
			.modifier(udo.getModifier())
			.build();

		CommunityRdo community = communityLogic.modifyCommunity(updatedUdo);
		return ResponseEntity.ok(ApiResponse.success("Community modified successfully", community));
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteCommunity(@PathVariable Integer id, @RequestParam Integer userId) {
		communityLogic.deleteCommunity(id, userId);
		return ResponseEntity.ok(ApiResponse.success("Community deleted successfully"));
	}

	@Override
	@PostMapping("/{id}/members")
	public ResponseEntity<ApiResponse<Void>> joinCommunity(
		@PathVariable Integer id,
		@Valid @RequestBody MemberJoinCdo cdo) {
		MemberJoinCdo updatedCdo = MemberJoinCdo.builder()
			.communityId(id)
			.userId(cdo.getUserId())
			.registrant(cdo.getRegistrant())
			.secretNumber(cdo.getSecretNumber())
			.build();

		communityLogic.joinMember(updatedCdo);
		return ResponseEntity.ok(ApiResponse.success("Successfully joined community"));
	}

	@Override
	@DeleteMapping("/{id}/members/{userId}")
	public ResponseEntity<ApiResponse<Void>> leaveCommunity(
		@PathVariable Integer id,
		@PathVariable Integer userId) {
		MemberLeaveCdo cdo = MemberLeaveCdo.builder()
			.communityId(id)
			.userId(userId)
			.build();

		communityLogic.leaveMember(cdo);
		return ResponseEntity.ok(ApiResponse.success("Successfully left community"));
	}

	@Override
	@GetMapping("/{id}/members/{userId}/check")
	public ResponseEntity<ApiResponse<Boolean>> checkMembership(
		@PathVariable Integer id,
		@PathVariable Integer userId) {
		boolean isMember = communityLogic.checkMembership(id, userId);
		return ResponseEntity.ok(ApiResponse.success("Membership status retrieved", isMember));
	}
}