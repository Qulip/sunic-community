package com.sunic.community.aggregate.community.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunic.community.aggregate.community.store.CommunityStore;
import com.sunic.community.aggregate.community.store.MemberStore;
import com.sunic.community.aggregate.proxy.UserProxy;
import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.entity.Member;
import com.sunic.community.spec.community.exception.MembershipException;
import com.sunic.community.spec.common.exception.UnauthorizedException;
import com.sunic.community.spec.community.facade.sdo.CommunityCdo;
import com.sunic.community.spec.community.facade.sdo.CommunityRdo;
import com.sunic.community.spec.community.facade.sdo.CommunityUdo;
import com.sunic.community.spec.community.facade.sdo.MemberJoinCdo;
import com.sunic.community.spec.community.facade.sdo.MemberLeaveCdo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityLogic {

	private final CommunityStore communityStore;
	private final MemberStore memberStore;
	private final UserProxy userProxy;

	@Transactional
	public CommunityRdo registerCommunity(CommunityCdo communityCdo) {
		validateAdminUser(communityCdo.getRegistrant());
		Community community = communityStore.save(Community.create(communityCdo));
		return convertToCommunityRdo(community);
	}

	@Transactional
	public CommunityRdo modifyCommunity(CommunityUdo modifySdo) {
		validateAdminUser(modifySdo.getModifier());
		Community community = communityStore.findById(modifySdo.getId());
		community.modify(modifySdo);
		Community updated = communityStore.update(community);
		return convertToCommunityRdo(updated);
	}

	@Transactional
	public void deleteCommunity(Integer communityId, Integer userId) {
		validateAdminUser(userId);
		communityStore.deleteById(communityId);
	}

	public CommunityRdo getCommunity(Integer communityId) {
		Community community = communityStore.findById(communityId);
		return convertToCommunityRdo(community);
	}

	public List<CommunityRdo> getAllCommunities() {
		return communityStore.findAll().stream().map(this::convertToCommunityRdo).collect(Collectors.toList());
	}

	@Transactional
	public void joinMember(MemberJoinCdo joinSdo) {
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

	@Transactional
	public void leaveMember(MemberLeaveCdo leaveSdo) {
		if (!memberStore.existsByUserIdAndCommunityId(leaveSdo.getUserId(), leaveSdo.getCommunityId())) {
			throw new MembershipException("User is not a member of this community");
		}

		memberStore.deleteByUserIdAndCommunityId(leaveSdo.getUserId(), leaveSdo.getCommunityId());

		Community community = communityStore.findById(leaveSdo.getCommunityId());
		Community updatedCommunity = community.removeMember();
		communityStore.update(updatedCommunity);
	}

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

	private void validateAdminUser(Integer userId) {
		if (userId == null) {
			throw new UnauthorizedException("User ID is required");
		}
		if (!userProxy.checkUserIsAdmin(userId)) {
			throw new UnauthorizedException("Admin privileges required for this operation");
		}
	}
}