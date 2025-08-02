package com.sunic.community.spec.community.entity;

import com.sunic.community.spec.community.facade.sdo.MemberJoinCdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Member {
	private Integer id;
	private Integer communityId;
	private Integer userId;
	private Long joinedTime;
	private Integer registrant;

	public static Member create(MemberJoinCdo sdo) {
		long currentTime = System.currentTimeMillis();
		return Member.builder()
			.communityId(sdo.getCommunityId())
			.userId(sdo.getUserId())
			.joinedTime(currentTime)
			.registrant(sdo.getRegistrant())
			.build();
	}
}